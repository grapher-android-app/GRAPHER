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
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.graph.SimpleGraph
import org.jgrapht.util.SupplierUtil
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
    private val def_node_color : Int = resources.getColor(R.color.teal_700, null)
    private val marked_node_color : Int = resources.getColor(R.color.teal_200, null)
    private val selected_node_color : Int = resources.getColor(R.color.purple_200, null)
    private val touched_node_color : Int = resources.getColor(R.color.node_color_standard, null)
    private val def_edge_color : Int = resources.getColor(R.color.purple_500, null)
    private val marked_edge_color : Int = resources.getColor(R.color.purple_700, null)

    private var highlightedNodes = HashSet<Node>()
    private var selectedNodes = HashSet<Node>()
    private var markedEdges = HashSet<Edge<Node>>()

    private var n1 = Node(Coordinate.ORIGO)
    private var n2 = Node(Coordinate.ZERO)
    private var graph : SimpleGraph<Node, Edge<Node>> = SimpleGraph({ Node(Coordinate.ORIGO) }, { Edge<Node>() }, false)

    private var gestureDetector: GestureDetector
    private var gestureListener: MyGestureListener

    private var nodeMode: Boolean = true
    private var selectedNode: Node? = null

    private var isScrolling = false

    constructor(context: Context?, attrs: AttributeSet): this(context, attrs, 0)

    private var graphWithMemory = Undo(graph)
    private var layout : SpringLayout? = null

    private var prevPointerCount: Int = 0
    private var prevPointerCoords: Array<Coordinate>? = null

    private var transformMatrix: Matrix = Matrix()
    private var prevMatrix: Matrix = Matrix()

    private var matrixScale: Float = 1F

    private var errorMissRadius: Float = 3F


    init {
        gestureListener = MyGestureListener()
        gestureDetector = GestureDetector(getContext(), gestureListener, handler)
        gestureDetector.setIsLongpressEnabled(true)
        edgePaint.color = resources.getColor(R.color.purple_200, null)
        edgePaint.strokeWidth = 8f
        vertexPaint.color = resources.getColor(R.color.node_color_standard, null)

        layout = SpringLayout(graph)
        layout!!.iterate(20)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean{
        if (event!=null && event.action == MotionEvent.ACTION_UP){ //stops scrolling
            if (isScrolling){
                Log.d("scroll", "stopped scrolling")
                isScrolling=false
                unselectNode()
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    fun changeMode(){
        nodeMode = !nodeMode
        clearAll()
        redraw()
        refreshDrawableState()
    }

    // TODO change to this implementation
    /**
     * Updates the color of all edges and nodes based on selection and highlighting
     * from user input and algorithm results
     */
    fun redraw() {
        if (graph.vertexSet() == null)  return

        for (node : Node in graph.vertexSet()) {
            node.setColor(if (nodeMode) def_node_color else touched_node_color)
            if (highlightedNodes.contains(node)) {
                node.setColor(marked_node_color)
            }
            if (selectedNodes.contains(node)) {
                node.setColor(selected_node_color)
            }
            if (!nodeMode && selectedNode!=null && selectedNode==node){
                node.setColor(selected_node_color)
            }
        }

        for (edge: Edge<Node> in graph.edgeSet()) {
            edge.setColor(def_edge_color)
            if (markedEdges.contains(edge)) {
                edge.setStyle(EdgeStyle.BOLD)
            }
            else {
                edge.setStyle(EdgeStyle.SOLID)
            }
        }
        invalidate()
    }

    private fun translateCoordinate(screenCoordinate: Coordinate): Coordinate {
        val screenPoint = floatArrayOf(screenCoordinate.getX(), screenCoordinate.getY())
        val invertedTransformMatrix = Matrix()
        transformMatrix.invert(invertedTransformMatrix)
        invertedTransformMatrix.mapPoints(screenPoint)
        return Coordinate(screenPoint[0], screenPoint[1])
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        edgePaint.strokeWidth = 5F
        edgePaint.style = Paint.Style.STROKE

        if (prevPointerCoords!=null) {
            vertexPaint.setColor(Color.RED)
            canvas.drawCircle(
                    prevPointerCoords!![0].getX(), prevPointerCoords!![0].getY(), 15F, vertexPaint
            )
            vertexPaint.setColor(Color.BLUE)
            canvas.drawCircle(
                    prevPointerCoords!![1].getX(), prevPointerCoords!![1].getY(), 15F, vertexPaint
            )
        }

        val m = matrix
        prevMatrix.set(m)
        m.preConcat(transformMatrix)
        canvas.setMatrix(m)



        vertexPaint.setColor(selected_node_color)
        for (e in graph.edgeSet()) {
            val source = e.getSource()
            val target = e.getTarget()
            val x1 = source.getCoordinate().getX()
            val y1 = source.getCoordinate().getY()
            val x2 = target.getCoordinate().getX()
            val y2 = target.getCoordinate().getY()

            if (e.getStyle() == EdgeStyle.BOLD) {
                edgePaint.color = marked_edge_color
                edgePaint.strokeWidth = 10F
                canvas.drawLine(x1, y1, x2, y2, edgePaint)
                edgePaint.strokeWidth = 5F
            }
            edgePaint.color = e.getColor()
            canvas.drawLine(x1, y1, x2, y2, edgePaint)
        }

        for (v in graph.vertexSet()) {
//            if (matrixScale<=errorMissRadius){
//                vertexPaint.color = Color.GRAY
//                canvas.drawCircle(
//                        v.getCoordinate().getX(), v.getCoordinate().getY(), v.getSize()*3F/matrixScale, vertexPaint
//                )
//                vertexPaint.color = resources.getColor(R.color.node_in_edge_mode_unselected, null)
//            }
            if (selectedNode!=null && selectedNode==v){
                vertexPaint.color = v.getColor()
                //vertexPaint.color = resources.getColor(R.color.node_in_edge_mode_selected, null)
                canvas.drawCircle(
                        v.getCoordinate().getX(), v.getCoordinate().getY(), v.getSize(), vertexPaint
                )
                vertexPaint.color = resources.getColor(R.color.node_in_edge_mode_unselected, null)
            }
            else {
                vertexPaint.color = v.getColor()
                canvas.drawCircle(
                        v.getCoordinate().getX(), v.getCoordinate().getY(), v.getSize(), vertexPaint
                )
            }
        }
        canvas.setMatrix(prevMatrix)
    }

    fun addNode(x: Float, y: Float) {
        addNode(Coordinate(x, y))
    }

    fun addNode(coordinate: Coordinate) {
        val vertex = Node(def_node_color, coordinate)
        graphWithMemory.addVertex(vertex)
        Log.d("NODE ADDED", graph.toString())
        invalidate()
        refreshDrawableState()
    }

    private fun getNodeAtCoordinate(coordinate: Coordinate): Node?{
        for (node: Node in graph.vertexSet()){
            if (isOnNode(coordinate, node)){
                return node
            }
        }
        return null
    }

    private fun markNode(node: Node) {
        selectedNodes.add(node)
        redraw()
    }

    private fun selectNode(node: Node){
        selectedNode = node
        redraw()
        refreshDrawableState()
    }

    private fun unselectNode(){
        selectedNode = null
        redraw()
        refreshDrawableState()
    }

    private fun hasSelectedNode(): Boolean = selectedNode!=null

    private fun isOnNode(coordinate: Coordinate, node: Node): Boolean{
        if (matrixScale<errorMissRadius){
            return node.getCoordinate().subtract(coordinate).length()<= node.getSize()*errorMissRadius/matrixScale
        }
        else{
            return node.getCoordinate().subtract(coordinate).length()<= node.getSize()
        }
    }


    private fun hasNode(coordinate: Coordinate): Boolean {
        for (node: Node in graph.vertexSet()){
            if (isOnNode(coordinate, node)){
                return true
            }
        }
        return false
    }

    private fun hasEdge(node: Node): Boolean = graph.containsEdge(selectedNode, node)

    private fun addEdgeBetween(node: Node){
//        val edge = Edge(selectedNode!!, node)
        graphWithMemory.addEdge(selectedNode!!, node)
        Log.d("EDGE ADDED", graph.toString())
        unselectNode()
        redraw()
        refreshDrawableState()
    }

    private fun removeEdge(u: Node, v: Node){
        graphWithMemory.removeEdge(u,v)
        unselectNode()
        redraw()
    }

    private fun moveNode(coordinate: Coordinate){
        selectedNode!!.setCoordinate(coordinate)
    }

    fun undo() : Boolean {
        val ret = graphWithMemory.undo()
        redraw()
        return ret
    }

    fun longShake(n: Int) {
        if (layout == null) {
            layout = SpringLayout(graph)
        }
        layout!!.iterate(n)
        invalidate()
    }

    fun shake() {
        if (layout == null) {
            layout = SpringLayout(graph)
        }
        longShake(20)
        invalidate()
    }

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (e != null){
                prevPointerCoords = null
                val coordinate = translateCoordinate(Coordinate(e.x,e.y))
                if (nodeMode){
                    if (!hasNode(coordinate)){
                        Log.d("OnSingleTapConfirmed", "Added Node")
                        addNode(coordinate)
                    }
                    else {
                        markNode(getNodeAtCoordinate(coordinate)!!)
                        Log.d("OnSingleTapConfirmed", "Selected Node")
                    }
                }
                else {
                    if (hasNode(coordinate)){
                        Log.d("OnSingleTapConfirmed", "Was Node")
                        val node = getNodeAtCoordinate(coordinate)!!
                        if (hasSelectedNode()){
                            if (node==selectedNode){ // Unselect node
                                Log.d("OnSingleTapConfirmed", "Unselected Node")
                                unselectNode()
                            }
                            else{
                                if (hasEdge(node)){ //Already edge between two selected nodes
                                    removeEdge(selectedNode!!,node)
                                    Log.d("OnSingleTapConfirmed", "Removed Edge")
                                }
                                else { //No edge
                                    addEdgeBetween(node)
                                    Log.d("OnSingleTapConfirmed", "Added Edge")
                                }
                            }
                        }
                        else {
                            Log.d("OnSingleTapConfirmed", "Selected Node")
                            selectNode(node)
                        }
                    }
                    else{
                        Log.d("OnSingleTapConfirmed", "Wasn't Node")
                    }
                }
                return true
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            Log.d("onDoubleTap", "onSingleTapConfirmed")
            if (nodeMode){

            } else{

            }
            return super.onDoubleTap(e)
        }

        override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
        ): Boolean {
            if (e1!=null && e2!=null){
                Log.d("POINTERCOUNT","POINTER COUNT IS ${e2.pointerCount}")
                if (e2.pointerCount==1){
                    prevPointerCoords = null
                    if (nodeMode) {
                        val coordinate = translateCoordinate(Coordinate(e2.x, e2.y))
                        if (isScrolling) {
                            moveNode(coordinate)
                        } else {
                            if (hasNode(coordinate)) {
                                val node = getNodeAtCoordinate(coordinate)
                                selectNode(node!!)
                                moveNode(coordinate)
                                isScrolling = true
                            }
                        }
                    }
                }
                else if (e2.pointerCount==2){ // 2 fingers
                    if (prevPointerCoords==null || prevPointerCount != 2) {
                        prevPointerCoords = arrayOf(Coordinate(e2.getX(0), e2.getY(0)), Coordinate(e2.getX(1), e2.getY(1)))
                    } else {
                        var newCoords = arrayOf(
                                Coordinate(e2.getX(0), e2.getY(0)),
                                Coordinate(e2.getX(1), e2.getY(1))
                        )
                        val VectorPrevious = prevPointerCoords!!.get(1).subtract(prevPointerCoords!!.get(0))
                        val VectorNew = newCoords[1].subtract(newCoords[0])
                        val diffAngle = VectorNew.angle() - VectorPrevious.angle()
                        val scale = VectorNew.length() / VectorPrevious.length()

                        // the transformations
                        transformMatrix.postTranslate(
                                -prevPointerCoords!!.get(0).getX(),
                                -prevPointerCoords!!.get(0).getY()
                        )
                        transformMatrix.postRotate(diffAngle)
                        transformMatrix.postScale(scale, scale)
                        matrixScale *= scale
                        transformMatrix.postTranslate(newCoords[0].getX(), newCoords[0].getY())
                        prevPointerCoords = newCoords
                    }
                } else { // 3 or more
                    prevPointerCoords = null
                    prevPointerCount=e2.pointerCount;
                    return false
                }
                prevPointerCount=e2.pointerCount;
                invalidate()
                return true
            }
            prevPointerCoords=null
            return false
        }

        override fun onLongPress(e: MotionEvent?) {
            Log.d("GESTURE LISTENER", "onLongPress")
            if (e!=null) {
            }
        }

        /**
         * This always returns true, because if it didn't, then long press would always be triggered
         */
        override fun onDown(e: MotionEvent?): Boolean{
            prevPointerCount=-1
            return true
        }
    }

//    override fun getController(): GestureController {
//        return GestureController(this)
//    }

    //TODO move to GraphViewController Class
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
        var powerGraph = PowerGraph.constructPowerGraph(graph)
        for (edge: Edge<Node> in powerGraph.edgeSet()){
            graphWithMemory.addEdge(edge)
        }
        redraw()
    }

    fun exactDominatingSet(){
        val eds = ExactDominatingSet(graph)
        val nodes = eds.execute()
        if (nodes != null) {
            for (node in nodes){
                highlightedNodes.add(node)
            }
        }
        redraw()
    }

    fun showCenterNode() : Boolean {
        clearAll()
        redraw()
        val center : Node = CenterInspector.getCenter(graph) ?: return false
        highlightedNodes.add(center)
        redraw()
        return true
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