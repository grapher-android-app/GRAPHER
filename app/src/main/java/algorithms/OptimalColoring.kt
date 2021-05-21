package algorithms

import org.jgrapht.graph.SimpleGraph
import util.Neighbors
import java.util.*
import kotlin.collections.HashSet

/**
 * Class for finding an optimal colouring of a graph.
 * Description of howto is found
 * in the comments of getcoloring method.
 * The main purpose is to return a set of sets,
 * where each set can be assigned
 * a separate colour.
 * @author H�vard Haug
 *
 * @param <V> node type
 * @param <E> edge type
</E></V> */
class OptimalColouring<V, E>(graph: SimpleGraph<V, E>?) :
    Algorithm<V, E, Set<Set<V>>?>(graph) {

    /**
     * Given the chromatic number,
     * divides graph into sets so that if
     * each set has separate colours,
     * no two nodes of same colour are adjacent.
     * @param graph
     * @return Sets that define a colouring of the graph
     */
    fun <V, E> getColoring(newgraph: SimpleGraph<V, E>): Set<Set<V>> {
        setProgressGoal(newgraph.vertexSet().size)
        val graph: SimpleGraph<V,E> = newgraph.clone() as SimpleGraph<V, E>
        val chrom: ChromaticNumber<V, E>
        chrom = ChromaticNumber<V, E>(graph)
        var k: Int = chrom.getChromaticNumber(graph)
        val divisions: MutableSet<Set<V>> = HashSet()
        if (newgraph.vertexSet().isEmpty()) return divisions
        println(k)
        //if k = 0,1 all vertices should get the same colour
        if (k == 1 || k == 0) {
            divisions.add(graph.vertexSet())
            return divisions
        }
        //if k = 2, use bipartite as it is much faster
        if (k == 2) {
            val half: HashSet<V>? = BipartiteInspector.getBipartition(graph)
            val vSetCopy: MutableSet<V> = HashSet()
            for (v in graph.vertexSet()) vSetCopy.add(v)
            vSetCopy.removeAll(half as Collection<V?>)
            val otherHalf = vSetCopy
            divisions.add(half)
            divisions.add(otherHalf)
            return divisions
        }
        /*
		 * For k > 2 use the following strategy:
		 * choose a vertex v, make it universal in the graph.
		 * Let k be the old chromatic number,
		 * k' the chromatic number of the edited graph.
		 * a) If k = k' v must have a different colour
		 *    from the rest of the graph,
		 *    so put it into its own colour category.
		 *    Remove v and search for k-1 colouring.
		 * b) if k' > k, enumerate all vertices
		 *    not incident to v
		 *    in the original graph as {u0, u1, u2 ... un}.
		 *    Let Gi = (V(G), E(G) U {vu0, vu1, .. vui}).
		 *    Want to find the smallest i
		 *    such that chromatic number of
		 *    Gi = k + 1, as v ui must have the same colour.
		 *    Then v and ui are merged into one vertex.
		 * Keep working with the resultant vertex
		 * until k = k' and then set all the merged vertices
		 * to the same colour and keep working until
		 * all vertices have been removed.
		 */
        var vertexSet: Set<V?> = HashSet(graph.vertexSet())
        var vIt = vertexSet.iterator()
        var v: V? = null
        var newVertex = true
        var vertices: MutableList<V> = ArrayList()
        var colourSet: MutableSet<V> = HashSet()
        while (vIt.hasNext() || newVertex == false) {
            increaseProgress()
            if (newVertex) {
                v = vIt.next()
            }
            vertices = ArrayList()
            for (v2 in vertexSet) {
                if (!graph.containsEdge(v, v2) && v != v2) vertices.add(v2!!)
            }
            var dummy: SimpleGraph<V?, E>
            dummy = graph.clone() as SimpleGraph<V?, E>
            for (v2 in vertices) {
                dummy.addEdge(v, v2)
            }
            if (chrom.getChromaticNumber(dummy) === k) {
                colourSet.add(v!!)
                divisions.add(colourSet)
                colourSet = HashSet()
                graph.removeVertex(v)
                k--
                newVertex = true
            } else {
                var upper = vertices.size - 1
                var lower = 0
                while (upper > lower) {
                    val mid = (upper + lower) / 2
                    dummy = graph.clone() as SimpleGraph<V?, E>
                    for (j in 0..mid) {
                        dummy.addEdge(v, vertices[j])
                    }
                    if (chrom.getChromaticNumber(dummy) > k) {
                        upper = mid
                    } else {
                        lower = mid + 1
                    }
                }
                colourSet.add(vertices[upper])
                mergeVertices(graph, v, vertices[upper])
                newVertex = false
            }
            vertexSet = HashSet(graph.vertexSet())
            vIt = vertexSet.iterator()
        }
        return divisions
    }

    /**
     * Merges v1 into v2
     * @param g graph
     * @param v1 vertex which is removed
     * @param v2 vertex where edges of v1 is added
     */
    private fun <V, E> mergeVertices(
        g: SimpleGraph<V, E>, v: V?, u: V
    ) {
        val v1neighbours = Neighbors.openNeighborhood(g, u)
        g.removeVertex(u)
        v1neighbours.remove(v)
        for (v1 in v1neighbours) g.addEdge(v, v1)
    }

    override fun call(): Set<Set<V>> {
        return getColoring(graph!!)
    }
}