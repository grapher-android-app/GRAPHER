package algorithms

import org.jgrapht.graph.SimpleGraph
import util.Neighbors
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class FlowInspector {
    companion object{
        fun <V, E> findFlow(graph: SimpleGraph<V,E>, source: V, target:V) : Pair<Int, Collection<E>> {
            val flowEdges : HashSet<Pair<V,V>> = HashSet<Pair<V,V>>()
            var flow: Int = 0

            while(flowIncreasingPath(graph, flowEdges, source, target))
                ++flow

            val edges = HashSet<E>()
            for (p : Pair<V, V> in flowEdges) {
                edges.add(graph.getEdge(p.first, p.second))
            }
            return Pair<Int, Collection<E>>(flow, edges)
        }

        private fun <V,E> flowIncreasingPath(graph: SimpleGraph<V, E>, flowEdges: HashSet<Pair<V,V>>, source: V,
                                             target: V) : Boolean {
            var prev = HashMap<V, V>()
            var next: Queue<V> = LinkedList<V>()

            //initialize search
            next.add(source)
            // Not null but source due to Kotlin no null argument
            prev[source] = source

            while(!next.isEmpty()) {
                val v : V = next.poll()

                if (v == target) break

                // Look at the neighborhood
                for (neighbor : V in Neighbors.openNeighborhood(graph, v)) {
                    // If the edge is not in flowEdges and the neighbors is not
                    // already visited we want to search the neighbors
                    if (!flowEdges.contains(Pair(v, neighbor)) && !prev.containsKey(neighbor)) {
                        next.add(neighbor)
                        prev[neighbor] = v
                    }
                }
            }
            // No path found
            if (!prev.containsKey(target)) {
                return false
            }

            // Updates flowEdges according to the path found
            var v : V = target
            while(v != source) {
                flowEdges.add(Pair(prev[v]!!, v))
                flowEdges.add(Pair(v, prev[v]!!))
                v = prev[v]!!
            }
            return true
        }
    }
}