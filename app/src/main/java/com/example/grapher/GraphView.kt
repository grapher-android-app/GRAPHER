package com.example.grapher

import algorithms.*
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import model.Edge
import model.EdgeStyle
import model.Node
import org.jgrapht.GraphPath
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.graph.SimpleGraph
import util.Coordinate
import util.Undo


/**
 * Class dedicated for all functionality directly related to information being displayed
 * in the view where the graph is drawn.
 */
class GraphView(context: Context?, attrs: AttributeSet, defStyleAttr: Int = 0) : View(
        context,
        attrs,
        defStyleAttr
){

    companion object {
        @Volatile var startTime : Long = 0
        @Volatile var isStarted : Boolean = false
        fun time(start: Boolean) {
            val now : Long = System.currentTimeMillis()
            if (start) {
                startTime = now
                isStarted = true
            }
            else {
                if (!isStarted) return
                val duration : Long = now - startTime
                val sec : Double = duration / 1000.0
                println("> $sec seconds")
                isStarted = false
            }
        }
    }

    // Paints can be moved to edge and vertex classes
    private val edgePaint = Paint()
    private val vertexPaint = Paint()

    // colors used for different types of nodes and edges
    val node_mode_node_color : Int = resources.getColor(R.color.node_color_standard, null)
    val node_mode_edge_color : Int = resources.getColor(R.color.edge_in_node_mode, null)

    private val edge_mode_node_color : Int = resources.getColor(R.color.node_in_edge_mode_unselected, null)
    private val edge_mode_edge_color : Int = resources.getColor(R.color.edge_in_edge_mode, null)

    private val marked_edge_color : Int = resources.getColor(R.color.edge_color_path, null)
    private val marked_node_color : Int = resources.getColor(R.color.marked_node_after_algorithm, null)
    private val selected_node_color : Int = resources.getColor(R.color.purple_200, null)

    private var highlightedNodes = HashSet<Node>()
    var selectedNodes = HashSet<Node>()
    private var markedEdges = HashSet<Edge<Node>>()

    var graph : SimpleGraph<Node, Edge<Node>> = SimpleGraph({ Node(Coordinate.ORIGO) }, { Edge<Node>() }, false)

    private lateinit var gestureDetector: GestureDetector
//    private var gestureListener: MyGestureListener

    var nodeMode: Boolean = true
    var selectedNode: Node? = null

    var isScrolling = false

    constructor(context: Context?, attrs: AttributeSet): this(context, attrs, 0)

    var graphWithMemory = Undo(graph)
    var transformMatrix: Matrix = Matrix()
    private var prevMatrix: Matrix = Matrix()
    init {
        edgePaint.color = resources.getColor(R.color.purple_200, null)
        edgePaint.strokeWidth = 8f
        vertexPaint.color = resources.getColor(R.color.node_color_standard, null)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean{
        if (event!=null && event.action == MotionEvent.ACTION_UP){ //stops scrolling
            if (isScrolling){
                Log.d("scroll", "stopped scrolling")
                isScrolling=false
                selectedNode = null
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    /**
     * flips between node and edge drawing mode
     */
    fun changeMode(){
        nodeMode = !nodeMode
        clearAll()
        redraw()
        refreshDrawableState()
    }

    fun setGestureDetector(gestureDetector: GestureDetector){
        this.gestureDetector = gestureDetector
    }

    /**
     * Updates the color of all edges and nodes based on selection and highlighting
     * from user input and algorithm results
     */
    fun redraw() {
        if (graph.vertexSet() == null)  return

        for (node : Node in graph.vertexSet()) {
            node.setColor(if (nodeMode) node_mode_node_color else edge_mode_node_color)
            if (highlightedNodes.contains(node)) {
                node.setColor(marked_node_color)
            }
            if (selectedNodes.contains(node)) {
                node.setColor(selected_node_color)
            }
            if (selectedNode!=null && selectedNode == node){
                node.setColor(selected_node_color)
            }
            if (!nodeMode && selectedNode!=null && selectedNode==node){
                node.setColor(selected_node_color)
            }
        }

        for (edge: Edge<Node> in graph.edgeSet()) {
            edge.setColor(if (nodeMode) node_mode_edge_color else edge_mode_edge_color)
            if (markedEdges.contains(edge)) {
                edge.setStyle(EdgeStyle.BOLD)
                edge.setColor(marked_edge_color)
            }
            else {
                edge.setStyle(EdgeStyle.SOLID)
            }
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        edgePaint.strokeWidth = 5F
        edgePaint.style = Paint.Style.STROKE

//        if (prevPointerCoords!=null) {
//            vertexPaint.setColor(Color.RED)
//            canvas.drawCircle(
//                    prevPointerCoords!![0].getX(), prevPointerCoords!![0].getY(), 15F, vertexPaint
//            )
//            vertexPaint.setColor(Color.BLUE)
//            canvas.drawCircle(
//                    prevPointerCoords!![1].getX(), prevPointerCoords!![1].getY(), 15F, vertexPaint
//            )
//        }

        //Handles Rotation, Zooming and Panning
        val m = matrix
        prevMatrix.set(m)
        m.preConcat(transformMatrix)
        canvas.setMatrix(m)

        //Paint edges
        vertexPaint.color = selected_node_color
        for (e in graph.edgeSet()) {
            val source = e.getSource()
            val target = e.getTarget()
            val x1 = source.getCoordinate().getX()
            val y1 = source.getCoordinate().getY()
            val x2 = target.getCoordinate().getX()
            val y2 = target.getCoordinate().getY()

            edgePaint.color = e.getColor()

            if (e.getStyle() == EdgeStyle.BOLD) {
                edgePaint.strokeWidth = 15F
                canvas.drawLine(x1, y1, x2, y2, edgePaint)
                edgePaint.strokeWidth = 5F
            }
            else{
                canvas.drawLine(x1, y1, x2, y2, edgePaint)
            }
        }
        //Paint nodes
        for (v in graph.vertexSet()) {
            vertexPaint.color = v.getColor()
            canvas.drawCircle(
                    v.getCoordinate().getX(), v.getCoordinate().getY(), v.getSize(), vertexPaint
            )
        }
        canvas.setMatrix(prevMatrix)
    }

    // TODO check if works on multiple cycles

    fun showAllCycle4() : Int {
        val cycles : Collection<List<Node>> = CycleInspector.findAllC4(graph)
        clearAll()
        for (cycle : List<Node> in cycles) {
            for (i : Int in cycle.indices) {
                val v : Node = cycle[i % cycle.size]
                val u : Node = cycle[(i + 1) % cycle.size]
                highlightedNodes.add(v)
                highlightedNodes.add(u)

                if (graph.containsEdge(v, u)) {
                    val e : Edge<Node> = graph.getEdge(v, u)
                    markedEdges.add(e)
                }
                else {
                    error("Strange, lacks edge for v=$v, u=$u")
                }
            }
        }
        redraw()
        return cycles.size
    }

    fun constructPower(){
        val powerGraph = PowerGraph.constructPowerGraph(graph)
        for (edge: Edge<Node> in powerGraph.edgeSet()){
            graphWithMemory.addEdge(edge)
        }
        redraw()
    }

    fun exactDominatingSet(graphActivity: GraphActivity){
        val eds = ExactDominatingSet<Node,Edge<Node>>(graph)
        val algoWrapper: AlgoWrapper<Collection<Node>?>
        algoWrapper = object : AlgoWrapper<Collection<Node>?>(graphActivity, eds) {
            override fun resultText(result: Collection<Node>?): String {
                clearAll()
                Log.d("RESULT","IS DONE")
                return if (result == null) {
                    "Algorithm was cancelled"
                } else {
                    for (node in result){
                        highlightedNodes.add(node)
                    }
                    redraw()
                    "Hamiltonian path"
                }
            }
        }
        Thread{algoWrapper.run()}.start()
    }

    fun showCenterNode() : Boolean {
        clearAll()
        redraw()
        val center : Node = CenterInspector.getCenter(graph) ?: return false
        highlightedNodes.add(center)
        redraw()
        return true
    }

    fun isBipartite(): Boolean{
        return BipartiteInspector.isBipartite(graph)
    }

    fun isEulerian(): Boolean {
        return EulerianInspector.isEulerian(graph)
    }

    fun showFlow() : Int {
        if (selectedNodes.size != 2) {
            return -1
        }
        val iter : Iterator<Node> = selectedNodes.iterator()
        val s : Node = iter.next()
        val t : Node = iter.next()

        if (!ConnectivityInspector<Node, Edge<Node>>(graph).pathExists(s, t)) {
            clearAll()
            redraw()
            return 0
        }
        clearAll()

        val flow = FlowInspector.findFlow(graph, s, t)
        val edges : Collection<Edge<Node>> = flow.second

        highlightedNodes.add(s)
        highlightedNodes.add(t)
        for (e : Edge<Node> in edges) {
            markedEdges.add(e)
        }
        redraw()
        return flow.first
    }

    fun showHamiltonianPath(graphActivity: GraphActivity) {
        val hamPathAlgo: Algorithm<Node, Edge<Node>, GraphPath<Node, Edge<Node>>?>
        val algoWrapper: AlgoWrapper<GraphPath<Node, Edge<Node>>>
        hamPathAlgo = HamiltonianPathInspector(graph)
        algoWrapper = object : AlgoWrapper<GraphPath<Node, Edge<Node>>>(graphActivity, hamPathAlgo) {
            override fun resultText(result: GraphPath<Node, Edge<Node>>?): String {
                clearAll()
                return if (result == null) {
                    "No hamiltonian path"
                } else {
                    markedEdges.addAll(result.edgeList)
                    redraw()
                    "Hamiltonian path"
                }
            }
        }
        //algoWrapper.setTitle("Computing hamiltonian path ...")
        Thread{algoWrapper.run()}.start()
    }

    fun showOptimalColoring(graphActivity: GraphActivity){
        val optColAlgo: Algorithm<Node,Edge<Node>,Set<Set<Node>>?> = OptimalColouring(graph)
        val algoWrapper = object : AlgoWrapper<Set<Set<Node>>>(graphActivity, optColAlgo) {
            override fun resultText(result: Set<Set<Node>>?): String {
                clearAll()
                return if (result == null) {
                    "No optimal coloring"
                } else {
                    var colorId = 0F
                    val n: Float = result.size.toFloat()
                    for (color in result){
                        for (node in color){
                            val hsl = FloatArray(3)
                            hsl[0] = (colorId/n)*255F
                            hsl[1] = 1F
                            hsl[2] = 1F
                            node.setColor(Color.HSVToColor(hsl))
                        }
                        colorId+=1
                    }
                    invalidate()
                    "Optimal coloring with ${result.size} colors"
                }
            }
        }
        //algoWrapper.setTitle("Computing hamiltonian path ...")
        Thread{algoWrapper.run()}.start()
    }

    fun showHamiltonianCycle(graphActivity: GraphActivity) {
        val hamcyc: Algorithm<Node, Edge<Node>, GraphPath<Node, Edge<Node>>?>
        val algoWrapper: AlgoWrapper<GraphPath<Node, Edge<Node>>>
        hamcyc = HamiltonianCycleInspector(graph)
        algoWrapper = object : AlgoWrapper<GraphPath<Node, Edge<Node>>>(graphActivity, hamcyc) {
            override fun resultText(result: GraphPath<Node, Edge<Node>>?): String {
                clearAll()
                return if (result == null) {
                    redraw()
                    "Not hamiltonian."
                } else {
                    markedEdges.addAll(result.edgeList)
                    redraw()
                    "Graph is hamiltonian"
                }
            }
        }
        algoWrapper.setTitle("Computing hamiltonian cycle ...")
        //this is ugly but runs the progressbar in a new thread
        Thread{algoWrapper.run()}.start()
    }

    fun diameterInsp(): Int?{
        return DiameterInspector.diameter(graph)
    }





    /**
     * Catch all method to call when reset graph to default representation
     */
    fun clearAll() {
        markedEdges.clear()
        selectedNodes.clear()
        highlightedNodes.clear()
        redraw()
    }
}