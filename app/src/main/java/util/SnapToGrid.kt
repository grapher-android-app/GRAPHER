package util

import model.Edge
import model.Node
import org.jgrapht.graph.SimpleGraph
import kotlin.math.min
import kotlin.math.max
import kotlin.math.round

class SnapToGrid {

    // removed static SQRT3 variable

    private var graph : SimpleGraph<Node, Edge<Node>>

    constructor (graph: SimpleGraph<Node, Edge<Node>>) {
        this.graph = graph
    }

    private fun arbitrary() : Node? {
        if (graph.vertexSet().isEmpty())
            return null
        else {
            return graph.vertexSet().iterator().next()
        }
    }

    fun snap() {
        val n :Int = graph.vertexSet().size
        if (n == 0)
            return

        val arb : Node? = arbitrary()
        if (arb != null) {
            val cArb : Coordinate = arb.getCoordinate()
            val size : Float = arb.getSize()

            var xMin : Float = cArb.getX()
            var xMax : Float = cArb.getY()
            var yMin : Float = cArb.getX()
            var yMax : Float = cArb.getY()

            for (v : Node in graph.vertexSet()) {
                xMin = min(xMin, v.getCoordinate().getX())
                xMax = max(xMax, v.getCoordinate().getX())

                yMin = min(yMin, v.getCoordinate().getY())
                yMax = max(yMax, v.getCoordinate().getY())
            }
            val xDiff = xMax - xMin
            val yDiff = yMax - yMin

            for (v : Node in graph.vertexSet()) {
                val c : Coordinate = v.getCoordinate()
                var cx = c.getX() - xMin
                var cy = c.getY() - yMin

                //Find out what this does exactly
                cx = (cx * 100 * size) / xDiff
                cy = (cy * 100 * size) / yDiff

                cx = (round(cx / 200) * 100)
                cy = (round(cy / 200) * 100)

                //Conclusion so far, seems like it just rounds off the coordinate
                //(12.2, 6.24) -> (12, 6)
                v.setCoordinate(Coordinate(cx, cy))
            }
        }
    }

}