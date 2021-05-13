package algorithms

import org.jgrapht.graph.SimpleGraph
import util.Neighbors

object SimplicialInspector {

    private fun <V, E> getSimplicialVertices(graph : SimpleGraph<V, E>) : Collection<V> {
        val simpls = HashSet<V>()

        for (v : V in graph.vertexSet()) {
            val neighs = ArrayList<V>()
            neighs.addAll(Neighbors.openNeighborhood(graph, v))

            var isSimplicial = true
            for (i in 0..neighs.size) {
                for (j in i+1..neighs.size) {
                    if (!graph.containsEdge(neighs[i], neighs[j])) {
                        isSimplicial = false
                        break
                    }
                }
                if (!isSimplicial) {
                    break
                }
            }
            if (isSimplicial) {
                simpls.add(v)
            }
        }
        return simpls
    }

    @SuppressWarnings("unchecked")
    fun <V, E> isChordal(graph : SimpleGraph<V, E>) : Boolean {
        val gg = graph.clone() as SimpleGraph<V, E>
        while (gg.vertexSet().size > 3) {
            val simpls : Collection<V> = getSimplicialVertices(gg)
            if (simpls.size > 1) {
                gg.removeAllVertices(simpls)
            }
            else {
                return false
            }
        }
        return true
    }
}