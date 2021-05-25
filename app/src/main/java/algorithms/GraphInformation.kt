package algorithms

import model.Edge
import model.Node
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.graph.SimpleGraph

object GraphInformation {

    fun graphInfo(graph : SimpleGraph<Node, Edge<Node>>) : String {
        val vertexCount = graph.vertexSet().size
        if (vertexCount == 0) {
            return "The empty graph"
        }
        val edgeCount = graph.edgeSet().size
        if (edgeCount == 0) {
            return if (vertexCount == 1) {
                "K1"
            } else {
                "The trivial graph on $vertexCount vertices"
            }
        }

        val inspector = ConnectivityInspector<Node, Edge<Node>>(graph)

        val isConnected : Boolean = inspector.isConnected
        var nc = 1
        if (!isConnected) {
            nc = inspector.connectedSets().size
        }

        val acyclic = GirthInspector.isAcyclic(graph)
        val isChordal = SimplicialInspector.isChordal(graph)
        /*
        val isInterval = SimpleToBasicWrapper<Node, Edge<Node>>(graph).getIntervalGraph() != null
         */
        val maxDegree = maxDegree(graph)
        val minDegree = minDegree(graph)

        var s = ""
        /*
        if (isInterval) s += "Interval: "
        else if (isChordal) += "Chordal: "
         */
        if (isConnected) s += if (acyclic) "Tree" else "Connected graph"
        else {
            s+= if (acyclic) "Forest" else "Disconnected graph"
            s += " ($nc components)"
        }

        s += " on $vertexCount vertices and $edgeCount edges."
        if (maxDegree == minDegree) {
            if (maxDegree == vertexCount - 1) s += "Complete, K_$vertexCount"
            else {
                if (maxDegree == 2)
                    s += if (isConnected) " Cycle, C_$vertexCount," else " union of cycles"

                val srw : RegularityInspector.StronglyRegularWitness? =
                        RegularityInspector<Node, Edge<Node>>(graph).isStronglyRegular

                // graph is strongly regular!
                s += if (srw != null) " $srw" else " $maxDegree-regular"
            }
        }
        else {
            s += " Max degree $maxDegree, min degree $minDegree"
        }
        return s
    }

    private fun maxDegree(graph : SimpleGraph<Node, Edge<Node>>) : Int {
        // TODO (from old app) What to do on empty graphs?
        var d = 0
        for (v : Node in graph.vertexSet()) d = d.coerceAtLeast(graph.degreeOf(v))
        return d
    }

    private fun minDegree(graph : SimpleGraph<Node, Edge<Node>>) : Int {
        // TODO (from old app) What to do on empty graphs?
        var d = graph.vertexSet().size
        for (v : Node in graph.vertexSet()) d = d.coerceAtMost(graph.degreeOf(v))
        return d
    }
}