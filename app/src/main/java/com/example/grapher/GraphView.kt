package com.example.grapher

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import model.Edge
import model.DefaultSupplier
import model.Node
import org.jgrapht.graph.SimpleGraph
import util.Coordinate

class GraphView(context : Context?, attrs: AttributeSet, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    // Paints can be moved to edge and vertex classes
    private val edgePaint = Paint()
    private val vertexPaint = Paint()

    private var edgeSup = DefaultSupplier<Edge<Node>>()
    private var graph : SimpleGraph<Node, Edge<Node>> =
        SimpleGraph(null, edgeSup, false)

    private var gestureDetector: GestureDetector
    private var gestureListener: MyGestureListener

    private var mode: Boolean = true
    private var selectedNode: Node? = null

    constructor(context: Context?, attrs: AttributeSet): this(context, attrs,0)

    init {
        gestureListener = MyGestureListener()
        gestureDetector = GestureDetector(getContext(),gestureListener,handler)
        gestureDetector.setIsLongpressEnabled(true)
        edgePaint.color = resources.getColor(R.color.purple_200, null)
        edgePaint.strokeWidth = 8f
        vertexPaint.color = resources.getColor(R.color.node_color_standard, null)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean = gestureDetector.onTouchEvent(event)

    fun changeMode(){
        mode = !mode
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
        if (mode){
            vertexPaint.color = resources.getColor(R.color.node_color_standard,null)
        } else {
            vertexPaint.color = resources.getColor(R.color.node_in_edge_mode_unselected,null)
        }
        for (v in graph.vertexSet()) {
            if (selectedNode!=null && selectedNode==v){
                vertexPaint.color = resources.getColor(R.color.node_in_edge_mode_selected,null)
                canvas.drawCircle(
                        v.getCoordinate().getX(), v.getCoordinate().getY(), v.getSize(), vertexPaint)
                vertexPaint.color = resources.getColor(R.color.node_in_edge_mode_unselected,null)
            }
            else {
                canvas.drawCircle(
                        v.getCoordinate().getX(), v.getCoordinate().getY(), v.getSize(), vertexPaint)
            }
        }
    }

    fun addNode(x : Float, y : Float) {
        addNode(Coordinate(x,y))
    }

    fun addNode(coordinate: Coordinate) {
        val vertex = Node(coordinate)
        graph.addVertex(vertex)
        invalidate()
        refreshDrawableState()
        Log.d("MAKE NODE","created node")
        //addEdgeToPrev(vertex)
        //prevVertex = vertex
    }

    private fun getNodeAtCoordinate(coordinate: Coordinate): Node?{
        for (node: Node in graph.vertexSet()){
            if (node.getCoordinate().subtract(coordinate).length()<=node.getSize()){
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

    private fun hasNode(coordinate: Coordinate): Boolean {
        for (node: Node in graph.vertexSet()){
            if (node.getCoordinate().subtract(coordinate).length()<=node.getSize()){
                return true
            }
        }
        return false
    }

    private fun hasEdge(node: Node): Boolean = graph.containsEdge(selectedNode,node)

    private fun addEdgeBetween(node: Node){
        val edge = Edge(selectedNode!!, node)
        graph.addEdge(selectedNode!!,node,edge)
        unselectNode()
        invalidate()
        refreshDrawableState()
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
            Log.d("GESTURE LISTENER","onSingleTapConfirmed")
            return super.onDoubleTap(e)
        }

        override fun onLongPress(e: MotionEvent?) {
            Log.d("GESTURE LISTENER","onLongPress")
            if (e!=null) {
                Log.d("GESTURE LISTENER","onLongPress2")
                Log.d("coord",""+e.x+", "+e.y)
                addNode(e.x, e.y)
            }
        }

        /**
         * This always returns true, because if it didn't, then long press would always be triggered
         */
        override fun onDown(e: MotionEvent?): Boolean = true
    }
}