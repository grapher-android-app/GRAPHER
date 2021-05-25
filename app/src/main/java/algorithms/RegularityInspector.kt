package algorithms

import org.jgrapht.graph.SimpleGraph
import util.InducedSubgraph.inducedSubgraphIteratorLargeToSmall
import util.Neighbors
import java.util.*


class RegularityInspector<V, E>(graph: SimpleGraph<V, E>?) : Algorithm<V, E, Collection<V>?>(graph) {
    val isRegular: Boolean
        get() = isRegular(graph)

    fun isRegular(h: SimpleGraph<V, E>?): Boolean {
        if (h!!.vertexSet().size == 0) return true
        val deg = h.degreeOf(h.vertexSet().iterator().next())
        for (v in h.vertexSet()) {
            if (h.degreeOf(v) != deg) {
                return false
            }
        }
        return true
    }

    /**
     * Returns -1 if non-regular, otherwise degree of all vertices.
     *
     * @return degree or -1 if non-regular
     */
    val regularity: Int
        get() = getRegularity(graph)

    /**
     * Returns -1 if non-regular, otherwise degree of all vertices.
     *
     * @return degree or -1 if non-regular
     */
    fun getRegularity(h: SimpleGraph<V, E>?): Int {
        if (h == null) throw NullPointerException("Input was null")
        if (h.vertexSet().size == 0) return 0
        val deg = h.degreeOf(h.vertexSet().iterator().next())
        for (v in h.vertexSet()) {
            if (h.degreeOf(v) != deg) {
                return -1
            }
        }
        return deg
    }

    override fun call(): Collection<V>? {
        val i: Iterator<SimpleGraph<V, E>?> = inducedSubgraphIteratorLargeToSmall(graph!!)
        while (i.hasNext()) {
            if (cancelFlag) return null
            val h = i.next()
            progress(graphSize() - h!!.vertexSet().size, graphSize())
            if (isRegular(h)) {
                val vertices: MutableSet<V> = HashSet()
                vertices.addAll(graph.vertexSet())
                vertices.removeAll(h.vertexSet())
                return vertices
            }
        }
        throw IllegalStateException("Cannot possibly come here: $graph")
    }

    /**
     * Returns a deletion set for obtaining a degree-regular graph. Returns null
     * if and only if there is no induced subgraph with given regularity degree.
     *
     * @param graph
     * @param degree
     * @return
     */
    fun regularDeletionSet(graph: SimpleGraph<V, E>, degree: Int): Collection<V>? {
        val i: Iterator<SimpleGraph<V, E>?> = inducedSubgraphIteratorLargeToSmall(graph)
        while (i.hasNext()) {
            val h = i.next()
            if (getRegularity(h) == degree) {
                val vertices: MutableSet<V> = HashSet()
                vertices.addAll(graph.vertexSet())
                vertices.removeAll(h!!.vertexSet())
                return vertices
            }
        }
        return null
    }// mu: non-adjacent common neighbors// lambda: adjacent common neighbors

    // C_5 is the least srg

    // need common neighbors of adjacent and non-adjacent.
    val isStronglyRegular: StronglyRegularWitness?
        get() {
            val n = graph!!.vertexSet().size

            // C_5 is the least srg
            if (n <= 4) return null
            val degree = graph.degreeOf(graph.vertexSet().iterator().next())
            val vertices = ArrayList<V>(n)
            for (v in graph.vertexSet()) {
                vertices.add(v)
                if (graph.degreeOf(v) != degree) return null
            }
            var commonAdj = -1
            var commonNonAdj = -1

            // need common neighbors of adjacent and non-adjacent.
            for (i in 0 until n) {
                val v = vertices[i]
                val vn: Collection<V> = Neighbors.openNeighborhood(graph, v)
                for (j in i + 1 until n) {
                    val u = vertices[j]
                    val un: Collection<V> = Neighbors.openNeighborhood(graph, u)
                    if (graph.containsEdge(v, u)) {
                        // lambda: adjacent common neighbors
                        var counter = 0
                        for (nabo in vn) {
                            if (un.contains(nabo)) counter++
                        }
                        if (commonAdj < 0) {
                            commonAdj = counter
                        } else if (commonAdj != counter) {
                            return null
                        }
                    } else {
                        // mu: non-adjacent common neighbors
                        var counter = 0
                        for (nabo in vn) {
                            if (un.contains(nabo)) counter++
                        }
                        if (commonNonAdj < 0) {
                            commonNonAdj = counter
                        } else if (commonNonAdj != counter) {
                            return null
                        }
                    }
                }
            }
            return StronglyRegularWitness(n, degree, commonAdj, commonNonAdj)
        }

    class StronglyRegularWitness(
            /**
             * Order of the graph V(G) (number of vertices).
             *
             * @return order of graph
             */
            val nu: Int,
            /**
             * Degree of the vertices in G
             *
             * @return
             */
            val kappa: Int,
            /**
             * Every two adjacent vertices have λ common neighbors.
             *
             * @return lambda, number of common neighbors
             */
            val lambda: Int,
            /**
             * Every two non-adjacent vertices have μ common neighbors.
             *
             * @return mu, number of common neighbors
             */
            val mu: Int) {

        override fun hashCode(): Int {
            val prime = 31
            var result = 1
            result = prime * result + kappa
            result = prime * result + lambda
            result = prime * result + mu
            result = prime * result + nu
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null) return false
            if (javaClass != other.javaClass) return false
            val o = other as StronglyRegularWitness
            if (kappa != o.kappa) return false
            if (lambda != o.lambda) return false
            if (mu != o.mu) return false
            return nu == o.nu
        }

        override fun toString(): String {
            return "srg($nu, $kappa, $lambda, $mu)"
        }

    }
}