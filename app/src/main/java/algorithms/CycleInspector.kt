package algorithms

import org.jgrapht.graph.SimpleGraph
import util.Neighbors

class CycleInspector {

    companion object {
        /**
         * Returns a set of all cycles, where each cycle is
         *
         * @param graph
         * @return
         */
        fun <V, E> findAllC4(graph: SimpleGraph<V, E>) : Collection<List<V>> {
            val nodes = ArrayList<V>(graph.vertexSet().size)

            val cycles = HashSet<List<V>>()
            nodes.addAll(graph.vertexSet())

            if (nodes.size < 4) {
                return cycles
            }
            for (i : Int in 0 until nodes.size) {
                val source : V = nodes[i]
                val sourceNeighbors : Collection<V> = Neighbors.openNeighborhood(graph, source)

                for (j : Int in i +1 until nodes.size) {
                    val target : V = nodes[j]
                    if (graph.containsEdge(source, target)) continue

                    // two non-adjacent nodes, testing if they are opposite corners of C4
                    val targetNeighbors : Collection<V> = Neighbors.openNeighborhood(graph, target)
                    val common = ArrayList<V>(targetNeighbors.size)
                    for (neighbor : V in targetNeighbors) {
                        if (sourceNeighbors.contains(neighbor)) {
                            common.add(neighbor)
                        }
                    }
                    for (k : Int in 0 until common.size) {
                        val x = common[k]

                        for (l : Int in k + 1 until common.size) {
                            val y = common[l]
                            if (!graph.containsEdge(x, y)) {
                                val cycle = ArrayList<V>(4)
                                cycle.add(source)
                                cycle.add(x)
                                cycle.add(target)
                                cycle.add(y)
                                cycles.add(cycle)
                            }
                        }
                    }
                }
            }
            return cycles
        }
    }
}