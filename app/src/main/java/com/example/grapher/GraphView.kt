package com.example.grapher

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.Switch
import model.Edge
import model.DefaultSupplier
import model.Node
import org.jgrapht.graph.SimpleGraph
import util.Coordinate

class GraphView(context : Context?, attrs: AttributeSet, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    // Paints can be moved to edge and vertex classes
    private val edgePaint = Paint()
    private val vertexPaint = Paint()

    private val origo =  Node(Coordinate(0f, 0f))
    private var prevVertex = origo

    private var edgeSup = DefaultSupplier<Edge<Node>>()
    private var graph : SimpleGraph<Node, Edge<Node>> =
        SimpleGraph(null, edgeSup, false)

    private var gestureDetector: GestureDetector
    private var gestureListener: MyGestureListener

    constructor(context: Context?, attrs: AttributeSet): this(context, attrs,0)

    init {
        gestureListener = MyGestureListener()
        gestureDetector = GestureDetector(getContext(),gestureListener,handler)
        gestureDetector.setIsLongpressEnabled(false)
        graph.addVertex(origo)
        val testNode = Node(Coordinate(553F,813F))
        graph.addVertex(testNode)
        edgePaint.color = resources.getColor(R.color.purple_200, null)
        edgePaint.strokeWidth = 8f
        vertexPaint.color = resources.getColor(R.color.teal_700, null)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean{
        Log.d("TOUCH", "TOUCH EVENT")
        return gestureDetector.onTouchEvent(event)
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

        for (v in graph.vertexSet()) {
            canvas.drawCircle(
                v.getCoordinate().getX(), v.getCoordinate().getY(), v.getSize(), vertexPaint)
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

    /**
     * Adds an edge from given vertex to the previous vertex
     * Simply used for edge drawing testing
     */
    private fun addEdgeToPrev(vertex : Node) {
        val edge = Edge(prevVertex, vertex)
        //TODO can only add edge by creating Default edge object
        //as supplier is not implemented correctly. Figure out to avoid workaround?
        graph.addEdge(prevVertex, vertex, edge)
    }

    private fun addEdge(vertex : Node) {
        val edge = Edge(prevVertex, vertex)
        //TODO can only add edge by creating Default edge object
        //as supplier is not implemented correctly. Figure out to avoid workaround?
        graph.addEdge(prevVertex, vertex, edge)
    }

    private fun getNodeAtCoordinate(coordinate: Coordinate): Node?{
        for (node: Node in graph.vertexSet()){
            if (node.getCoordinate().subtract(coordinate).length()>=node.getSize()*node.getSize()){
                return node
            }
        }
        return null
    }

    private fun hasNode(coordinate: Coordinate): Boolean {
        for (node: Node in graph.vertexSet()){
            if (node.getCoordinate().subtract(coordinate).length()>=node.getSize()*node.getSize()){
                return true
            }
        }
        return false
    }

    /**
     * Adds an edge from given vertex to a premade vertex
     * placed in origo. Simply used for edge drawing testing
     */
    private fun addEdgeOrigo(vertex : Node) {
        val edge = Edge(origo, vertex)
        //TODO can only add edge by creating Default edge object
        //as supplier is not implemented correctly. Figure out to avoid workaround?
        graph.addEdge(origo, vertex, edge)
    }

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            Log.d("GESTURE LISTENER","On SingleTapUp")
            if (e != null){
                Log.d("GESTURE LISTENER","On SingleTapUp2")
                addNode(e.x, e.y)
                return true
            }
            return false
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            Log.d("GESTURE LISTENER","onSingleTapConfirmed")
            if (e != null){
                Log.d("GESTURE LISTENER","On SingleTapConfirmed2")
                addNode(e.x, e.y)
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

        override fun onDown(e: MotionEvent?): Boolean {
            Log.d("GESTURE LISTENER","onSingleTapConfirmed")
            if (e != null){
                Log.d("GESTURE LISTENER","On SingleTapConfirmed2")
                addNode(e.x, e.y)
                return true
            }
            return false
        }
    }
}