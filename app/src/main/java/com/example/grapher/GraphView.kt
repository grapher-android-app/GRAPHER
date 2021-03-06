package com.example.grapher

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import model.Edge
import model.DefaultSupplier
import model.Node
import org.jgrapht.graph.SimpleGraph
import util.Coordinate

class GraphView(context : Context?) : View(context) {

    // Paints can be moved to edge and vertex classes
    private val edgePaint = Paint()
    private val vertexPaint = Paint()

    private val origo =  Node(Coordinate(0f, 0f))
    private var prevVertex = origo

    private var edgeSup = DefaultSupplier<Edge<Node>>()
    private var graph : SimpleGraph<Node, Edge<Node>> =
        SimpleGraph(null, edgeSup, false)

    init {
        graph.addVertex(origo)
        edgePaint.color = resources.getColor(R.color.purple_200, null)
        edgePaint.strokeWidth = 8f
        vertexPaint.color = resources.getColor(R.color.teal_700, null)
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

    fun addVertex(x : Float, y : Float) {
        val vertex = Node(Coordinate(x, y))
        graph.addVertex(vertex)
        addEdgeToPrev(vertex)
        prevVertex = vertex
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
}