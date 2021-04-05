package com.example.grapher

import algorithms.CenterInspector
import algorithms.CycleInspector
import algorithms.SpringLayout
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.alexvasilkov.gestures.GestureController
import com.alexvasilkov.gestures.views.interfaces.GestureView
import model.Edge
import model.Node
import org.jgrapht.graph.SimpleGraph
import util.Coordinate
import util.Undo
import java.util.function.Supplier

class GraphView(context : Context?, attrs: AttributeSet, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr), GestureView {

    companion object {
        @Volatile var startTime : Long = 0
        @Volatile var isStarted : Boolean = false
        fun time(start : Boolean) {
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
    private val def_marked_color : Int = resources.getColor(R.color.purple_700, null)

    private var hightlightedNodes = HashSet<Node>()
    private var selectedNodes = HashSet<Node>()
    private var markedEdges = HashSet<Edge<Node>>()

    private var n1 = Node(Coordinate.ORIGO)
    private var n2 = Node(Coordinate.ZERO)
    private var edgeSup = Supplier {Edge(n1, n2)}
    private var graph : SimpleGraph<Node, Edge<Node>> = SimpleGraph(null, edgeSup, false)

    private var gestureDetector: GestureDetector
    private var gestureListener: MyGestureListener

    private var mode: Boolean = true
    private var selectedNode: Node? = null

    private var isScrolling = false
    private var gestureController: GestureController

    constructor(context: Context?, attrs: AttributeSet): this(context, attrs,0)

    private var graphWithMemory = Undo(graph)

    private var layout : SpringLayout? = null

    init {
        gestureListener = MyGestureListener()
        gestureDetector = GestureDetector(getContext(),gestureListener,handler)
        gestureDetector.setIsLongpressEnabled(true)
        gestureController = GestureController(this)
        edgePaint.color = resources.getColor(R.color.purple_200, null)
        edgePaint.strokeWidth = 8f
        vertexPaint.color = resources.getColor(R.color.node_color_standard, null)
        gestureController.settings.isRotationEnabled=true
        gestureController.settings.isRestrictRotation = false
        gestureController.settings.setMaxZoom(3f).minZoom = 0.25F
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean{
        if (event!=null && event.action.equals(MotionEvent.ACTION_UP)){ //stops scrolling
            if (isScrolling){
                Log.d("scroll","stopped scrolling")
                isScrolling=false
                unselectNode()
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    fun changeMode(){
        mode = !mode
        invalidate()
        refreshDrawableState()
    }

    // TODO change to this implementation
    /**
     * Updates the color of all edges and nodes based on selection and highlighting
     * from user input and algorithm results
     */
    fun redraw() {
        if (graph.vertexSet() == null)  {
            return
        }

        for (node : Node in graph.vertexSet()) {
            node.setColor(if (mode) def_node_color else touched_node_color)
            if (hightlightedNodes.contains(node)) {
                node.setColor(marked_node_color)
            }
            if (selectedNodes.contains(node)) {
                node.setColor(selected_node_color)
            }
        }

        for (edge: Edge<Node> in graph.edgeSet()) {
            if (markedEdges.contains(edge)) {
                edge.setColor(def_marked_color)
            }
            else {
                edge.setColor(def_edge_color)
            }
        }
        invalidate()
    }

    override fun onDraw(canvas : Canvas) {
        super.onDraw(canvas)
        for (e in graph.edgeSet()) {
            val source = e.getSource()
            val target = e.getTarget()
            val x1 = source.getCoordinate().getX()
            val y1 = source.getCoordinate().getY()
            val x2 = target.getCoordinate().getX()
            val y2 = target.getCoordinate().getY()

            canvas.drawLine(x1, y1, x2, y2, edgePaint)
        }
        /*
        if (mode){
            vertexPaint.color = resources.getColor(R.color.node_color_standard,null)
        } else {
            vertexPaint.color = resources.getColor(R.color.node_in_edge_mode_unselected,null)
        }
         */
        for (v in graph.vertexSet()) {
            if (selectedNode!=null && selectedNode==v){
                vertexPaint.color = resources.getColor(R.color.node_in_edge_mode_selected,null)
                canvas.drawCircle(
                        v.getCoordinate().getX(), v.getCoordinate().getY(), v.getSize(), vertexPaint)
                vertexPaint.color = resources.getColor(R.color.node_in_edge_mode_unselected,null)
            }
            else {
                vertexPaint.color = v.getColor()
                canvas.drawCircle(
                        v.getCoordinate().getX(), v.getCoordinate().getY(), v.getSize(), vertexPaint)
            }
        }
    }

    fun addNode(x : Float, y : Float) {
        addNode(Coordinate(x,y))
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
            if (isOnNode(coordinate,node)){
                return node
            }
        }
        return null
    }

    private fun selectNode(node: Node){
        selectedNode = node
        invalidate()
        refreshDrawableState()
    }

    private fun unselectNode(){
        selectedNode = null
        invalidate()
        refreshDrawableState()
    }

    private fun hasSelectedNode(): Boolean = selectedNode!=null

    private fun isOnNode(coordinate: Coordinate, node: Node): Boolean = node.getCoordinate().subtract(coordinate).length()<=node.getSize()*2

    private fun hasNode(coordinate: Coordinate): Boolean {
        for (node: Node in graph.vertexSet()){
            if (isOnNode(coordinate,node)){
                return true
            }
        }
        return false
    }

    private fun hasEdge(node: Node): Boolean = graph.containsEdge(selectedNode,node)

    private fun addEdgeBetween(node: Node){
        val edge = Edge(selectedNode!!, node)
        graphWithMemory.addEdge(selectedNode!!,node, edge)
        Log.d("EDGE ADDED", graph.toString())
        unselectNode()
        invalidate()
        refreshDrawableState()
    }

    private fun moveNode(distanceX: Float, distanceY: Float){
        val coordinate = Coordinate(distanceX,distanceY)
        selectedNode!!.setCoordinate(selectedNode!!.getCoordinate().subtract(coordinate))
        invalidate()
        refreshDrawableState()
    }

    fun undo() : Boolean {
        val ret = graphWithMemory.undo()
        invalidate()
        return ret
    }

    fun longShake(n : Int) {
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
                val coordinate = Coordinate(e.x,e.y)
                if (mode){
                    if (!hasNode(coordinate)){
                        Log.d("OnSingleTapConfirmed","Added Node")
                        addNode(e.x, e.y)
                    }
                    else {
                        Log.d("OnSingleTapConfirmed", "Didn't add Node")
                    }
                }
                else {
                    if (hasNode(coordinate)){
                        Log.d("OnSingleTapConfirmed","Was Node")
                        val node = getNodeAtCoordinate(coordinate)!!
                        if (hasSelectedNode()){
                            if (node==selectedNode){
                                Log.d("OnSingleTapConfirmed","Unselected Node")
                                unselectNode()
                            }
                            else{
                                if (hasEdge(node)){
                                    Log.d("OnSingleTapConfirmed","Didn't add Edge")
                                }
                                else {
                                    addEdgeBetween(node)
                                    Log.d("OnSingleTapConfirmed","Added Edge")
                                }
                            }
                        }
                        else {
                            Log.d("OnSingleTapConfirmed","Selected Node")
                            selectNode(node)
                        }
                    }
                    else{
                        Log.d("OnSingleTapConfirmed","Wasn't Node")
                    }
                }
                return true
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            Log.d("onDoubleTap","onSingleTapConfirmed")
            if (mode){

            } else{

            }
            return super.onDoubleTap(e)
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (e1!=null && e2!=null){
                if (mode) {
                    if (isScrolling) {
                        moveNode(distanceX, distanceY)
                    } else {
                        val coordinate = Coordinate(Coordinate(e1.x, e1.y))
                        if (hasNode(coordinate)) {
                            val node = getNodeAtCoordinate(coordinate)
                            selectNode(node!!)
                            moveNode(distanceX, distanceY)
                            isScrolling = true
                        }
                    }
                }
                Log.d("OnScroll","Scroll")
                return true
            }
            return false
        }

        override fun onLongPress(e: MotionEvent?) {
            Log.d("GESTURE LISTENER","onLongPress")
            if (e!=null) {
            }
        }

        /**
         * This always returns true, because if it didn't, then long press would always be triggered
         */
        override fun onDown(e: MotionEvent?): Boolean = true
    }

    override fun getController(): GestureController {
        return gestureController
    }

    //TODO move to GraphViewController Class
    // TODO check if works on multiple cycles

    fun showAllCycle4() : Int {
        val cycles : Collection<List<Node>> = CycleInspector.findAllC4(graph)
        clearAll()
        for (cycle : List<Node> in cycles) {
            for (i : Int in cycle.indices) {
                val v : Node = cycle[i % cycle.size]
                val u : Node = cycle[(i + 1) % cycle.size]
                hightlightedNodes.add(v)
                hightlightedNodes.add(u)

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

    fun showCenterNode() : Boolean {
        clearAll()
        redraw()
        val center : Node = CenterInspector.getCenter(graph) ?: return false
        hightlightedNodes.add(center)
        redraw()
        return true
    }

    fun clearAll() {
        markedEdges.clear()
        selectedNodes.clear()
        hightlightedNodes.clear()
        redraw()
    }
}