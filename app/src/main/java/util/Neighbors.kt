package util

import org.jgrapht.graph.SimpleGraph
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Keeps track of Neighborhoods in Graph
 */
class Neighbors {

    // static
    companion object {
        fun <V, E> closedNNeighborhood(graph: SimpleGraph<V, E>, node : V, n : Int): HashSet<V> {
            val neighbors =  HashSet<V>(graph.degreeOf(node))
            neighbors.add(node)
            for (i in 0..n) {
                val newNeighbors = HashSet<V>(graph.vertexSet().size)
                for (v in neighbors) {
                    newNeighbors.addAll(openNeighborhood(graph, v))
                }
                neighbors.addAll(newNeighbors)
            }
            return neighbors
        }

        fun <V, E> openNNeighborhood(graph: SimpleGraph<V, E>, node : V, n : Int): HashSet<V> {
            val neighbors = HashSet<V>(graph.degreeOf(node))
            neighbors.add(node)
            for (i in 0..n) {
                val newNeighbors = HashSet<V>(graph.vertexSet().size)
                for (v in neighbors) {
                    newNeighbors.addAll(openNeighborhood(graph, v))
                }
                neighbors.addAll(newNeighbors)
            }
            neighbors.remove(node)
            return neighbors
        }

        fun <V, E> closedNeighborhood(graph: SimpleGraph<V, E>, node : V) : Collection<V> {
            val set = HashSet<V>(graph.vertexSet().size)
            set.add(node)
            for (edge in graph.edgesOf(node)) {
                val neighbor = opposite(graph, node, edge)
                set.add(neighbor)
            }
            return set
        }

        fun <V, E> closedNeighborhood(graph: SimpleGraph<V, E>, nodes : Collection<V>) : Collection<V> {
            val set = HashSet<V>(graph.vertexSet().size)
            for (node in nodes) {
                set.add(node)
                for (edge in graph.edgesOf(node)) {
                    val neighbor = opposite(graph, node, edge)
                    set.add(neighbor)
                }
            }
            return set
        }

        fun <V, E> openNeighborhood(graph : SimpleGraph<V, E>, node : V): HashSet<V> {
            val set = HashSet<V>(graph.degreeOf(node))
            for (edge in graph.edgesOf(node)) {
                set.add(opposite(graph, node, edge))
            }
            return set
        }

        fun <V, E> openNeighborhood(graph : SimpleGraph<V, E>, nodes : Collection<V>): Collection<V> {
            val set = HashSet<V>(graph.vertexSet().size)
            for (node in nodes) {
                for (edge in graph.edgesOf(node)) {
                    val neighbor : V = opposite(graph, node, edge)
                    if (!nodes.contains(neighbor)) {
                        set.add(neighbor)
                    }
                }
            }
            return set
        }

        fun <V, E> orderedOpenNeighborhood(graph : SimpleGraph<V, E>, node : V, asc : Boolean) {
            val neighborhood : Collection<V> = openNeighborhood(graph, node)
            val list = ArrayList<V>(neighborhood.size)
            list.addAll(neighborhood)
            var comparator = object : Comparator<V> {
                override fun compare(a: V, b: V): Int {
                    return if (asc) {
                        graph.degreeOf(b) - graph.degreeOf(a)
                    } else {
                        return graph.degreeOf(a) - graph.degreeOf(b)
                    }
                }
            }
            Collections.sort(list, comparator)

        }

        fun <V, E> getNeighbor(graph : SimpleGraph<V, E>, node : V) : V {
            for (edge in graph.edgesOf(node)) {
                return (opposite(graph, node, edge))
            }
            //TODO this was originally null but not allowed in Kotlin, what to do?
            return node
        }

        fun <V, E> opposite(graph : SimpleGraph<V, E>, node : V, edge : E) : V {
            if (graph.getEdgeSource(edge) === node)
                return graph.getEdgeTarget(edge)
            return graph.getEdgeSource(edge)
        }

        fun <V, E> isIncident(graph: SimpleGraph<V, E>, node : V, edge: E) : Boolean {
            return node === graph.getEdgeSource(edge) || node === graph.getEdgeTarget(edge)
        }

        fun <V, E> isIncidentEdge(graph: SimpleGraph<V, E>, edge1 : E, edge2 : E) : Boolean {
            val v1 = graph.getEdgeSource(edge1)
            val v2 = graph.getEdgeTarget(edge1)
            return isIncident(graph, v1, edge2) || isIncident(graph, v2, edge2)
        }

        fun <V, E> sortByDegree(graph: SimpleGraph<V, E>, nodes : Collection<V>, asc : Boolean) : List<V> {
            var lst = ArrayList<V>(nodes)
            var comparator = object : Comparator<V> {
                override fun compare(a: V, b: V): Int {
                    val degA = graph.degreeOf(a)
                    val degB = graph.degreeOf(b)
                    if (degA == degB)
                        return 0
                    var ret : Int = 1
                    if (degA < degB) {
                        ret =  -1
                    }
                    return if (asc) {
                        ret
                    } else {
                        -ret
                    }
                }
            }
            Collections.sort(lst, comparator)
            return lst
        }
    }
}