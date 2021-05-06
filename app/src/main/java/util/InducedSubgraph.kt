package util

import org.jgrapht.graph.SimpleGraph

object InducedSubgraph {
    /**
     * Returns the induced subgraph by given vertices. Reuses the vertex
     * objects, but makes new edges.
     *
     * @param graph
     * @param vertices
     * @return induced graph graph[vertices]
     */
    fun <V, E> inducedSubgraphOf(
            graph: SimpleGraph<V, E>, vertices: Collection<V>): SimpleGraph<V, E> {
        val h = SimpleGraph<V, E>(graph.vertexSupplier,graph.edgeSupplier,false)
        for (v in vertices) {
            h.addVertex(v)
        }
        for (e in graph.edgeSet()) {
            val s = graph.getEdgeSource(e)
            val t = graph.getEdgeTarget(e)
            if (h.containsVertex(s) && h.containsVertex(t)) {
                h.addEdge(s, t)
            }
        }
        return h
    }

    fun <V, E> inducedSubgraphIterator(
            graph: SimpleGraph<V, E>): MutableIterator<SimpleGraph<V, E>?> {
        return object : MutableIterator<SimpleGraph<V, E>?> {
            var subsets: PowersetIterator<V> = PowersetIterator<V>(
                    graph.vertexSet())

            override fun hasNext(): Boolean {
                return subsets.hasNext()
            }

            override fun next(): SimpleGraph<V, E> {
                val vertices: Collection<V> = subsets.next()
                return inducedSubgraphOf(graph, vertices)
            }

            override fun remove() {
                throw UnsupportedOperationException(
                        "Cannot remove a set using this iterator")
            }
        }
    }

    fun <V, E> inducedSubgraphIteratorLargeToSmall(
            graph: SimpleGraph<V, E>): MutableIterator<SimpleGraph<V, E>?> {
        return object : MutableIterator<SimpleGraph<V, E>?> {
            var subsets: PowersetIterator.PowersetIteratorDescending<V> = PowersetIterator.PowersetIteratorDescending<V>(
                    graph.vertexSet())

            override fun hasNext(): Boolean {
                return subsets.hasNext()
            }

            override fun next(): SimpleGraph<V, E> {
                val vertices = subsets.next()
                return inducedSubgraphOf(graph, vertices)
            }

            override fun remove() {
                throw UnsupportedOperationException(
                        "Cannot remove a set using this iterator")
            }
        }
    }
}