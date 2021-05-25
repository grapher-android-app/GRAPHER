package algorithms

import model.Edge
import org.jgrapht.graph.SimpleGraph
import util.Neighbors

object GirthInspector {

    fun <V, E> isAcyclic(graph : SimpleGraph<V, E>) : Boolean {
        return girth(graph) < 0
    }

    /**
     * Returns the girth (length of shortest cycle) of the graph,
     * or -1 if no cycles.
     *
     * @param graph input graph
     * @return girth of graph or -1 if acyclic
     */
    fun <V, E> girth(graph : SimpleGraph<V, E>) : Int {
        var girth = graph.vertexSet().size + 1

        // copy of graph but with PathNodes
        val copy = SimpleGraph<PathNode<V>, Edge<PathNode<V>>>(null, { Edge() }, false)

        // map between original and copy graph
        val map = HashMap<V, PathNode<V>>()

        for (v : V in graph.vertexSet()) {
            val pv = PathNode(v)
            copy.addVertex(pv)
            map[v] = pv
        }

        for (e : E in graph.edgeSet()) {
            val u : PathNode<V> = map[graph.getEdgeSource(e)]!!
            val v : PathNode<V> = map[graph.getEdgeTarget(e)]!!
            val edge = copy.addEdge(u, v)
            edge.setSource(u)
            edge.setTarget(v)
        }

        // sets being
        val S = HashSet<PathNode<V>>()
        val R = HashSet<PathNode<V>>()

        for (v : PathNode<V> in copy.vertexSet()) {
            S.clear()
            R.add(v)

            while(R.isNotEmpty()) {
                val x : PathNode<V> = R.iterator().next()
                S.add(x)
                R.remove(x)

                for (y : PathNode<V> in Neighbors.openNeighborhood(copy, x)) {
                    if (y == x.parent) {
                        continue
                    }
                    if (!S.contains(y)) {
                        y.parent = x
                        y.dist = x.dist + 1
                        R.add(y)
                    }
                    else {
                        val dx : Int = x.dist
                        val dy : Int = y.dist
                        girth = girth.coerceAtMost(dx + dy + 1)
                    }
                }
            }
        }

        if (girth > graph.vertexSet().size) {
            return -1
        }
        return girth
    }

    class PathNode<V>(val original : V) {
        var parent: PathNode<V>? = null
        var dist = 0
    }
}