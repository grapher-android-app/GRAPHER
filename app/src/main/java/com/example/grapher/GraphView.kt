package com.example.grapher

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import model.DefaultEdge
import model.DefaultSupplier
import model.DefaultVertex
import org.jgrapht.graph.SimpleGraph
import util.Coordinate

class GraphView(context : Context?) : View(context) {

    private val edgePaint = Paint()
    private val origo =  DefaultVertex(Coordinate(0f, 0f))
    private var prevVertex = origo

    private var edgeSup = DefaultSupplier<DefaultEdge<DefaultVertex>>()
    private var graph : SimpleGraph<DefaultVertex, DefaultEdge<DefaultVertex>> =
        SimpleGraph(null, edgeSup, false)

    init {
        graph.addVertex(origo)
        edgePaint.color = Color.GREEN
        edgePaint.strokeWidth = 4f
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
    }

    fun addVertex(x : Float, y : Float) {
        val vertex = DefaultVertex(Coordinate(x, y))
        graph.addVertex(vertex)
        addEdgeToPrev(vertex)
        prevVertex = vertex
    }

    /**
     * Adds an edge from given vertex to the previous vertex
     * Simply used for edge drawing testing
     */
    private fun addEdgeToPrev(vertex : DefaultVertex) {
        val edge = DefaultEdge(prevVertex, vertex)
        //TODO can only add edge by creating Default edge object
        //as supplier is not implemented correctly. Figure out to avoid workaround?
        graph.addEdge(prevVertex, vertex, edge)
    }

    /**
     * Adds an edge from given vertex to a premade vertex
     * placed in origo. Simply used for edge drawing testing
     */
    private fun addEdgeOrigo(vertex : DefaultVertex) {
        val edge = DefaultEdge(origo, vertex)
        //TODO can only add edge by creating Default edge object
        //as supplier is not implemented correctly. Figure out to avoid workaround?
        graph.addEdge(origo, vertex, edge)
    }
}