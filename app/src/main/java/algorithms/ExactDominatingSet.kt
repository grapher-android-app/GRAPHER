package algorithms

import util.InducedSubgraph
import util.PowersetIterator
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.graph.SimpleGraph
import util.Neighbors
import java.util.*
import java.util.function.Supplier
import kotlin.collections.HashSet

/**
 * 2 to the n implementation of this W[2]-hard problem.
 *
 * Extremely slow implementation, many easy improvements, do connected
 * components individually, try sets in order of increasing size, adding
 * vertices of degree at least that size etc.
 *
 *
 * @author pgd
 */
class ExactDominatingSet<V, E>(graph: SimpleGraph<V, E>?) : Algorithm<V, E, Collection<V>?>(graph) {


    fun execute(cc: SimpleGraph<V, E>): HashSet<V>? {
        val g = SimpleGraph<VertexDominated<V>,EdgeDominated>(EdgeDominated::class.java)
        val map = HashMap<V, VertexDominated<V>>()
        for (v in cc.vertexSet()) {
            val vd = VertexDominated(v)
            map[v] = vd
            g.addVertex(vd)
        }
        for (e in cc.edgeSet()) {
            g.addEdge(map[cc.getEdgeSource(e)], map[cc.getEdgeTarget(e)], EdgeDominated())
        }
        val pi: PowersetIterator<VertexDominated<V>> = PowersetIterator<VertexDominated<V>>(g.vertexSet())
        var domset: Collection<VertexDominated<V>>? = null
        progress(0, cc.vertexSet().size)
        while (pi.hasNext()) {
            val current: Collection<VertexDominated<V>> = pi.next()

            // if domset is a smaller dom. set, we continue searching
            if (domset != null && current.size >= domset.size) {
                continue
            }
            if (cancelFlag) return null
            progress(current.size, cc.vertexSet().size)

            // test if current is a d.s.
            if (isDominatingSet(g, current.toHashSet())) {
                domset = current
            }
        }
        val res: HashSet<V> = HashSet(domset!!.size)
        for (vd in domset) {
            res.add(vd.vertex!!)
        }
        return res
    }

    private fun isDominatingSet(graph: SimpleGraph<VertexDominated<V>, EdgeDominated>, set: Collection<VertexDominated<V>>): Boolean {
        for (v in graph.vertexSet()) {
            v!!.dominated = false
        }
        for (dominator in set) {
            dominator.dominated = true
            for (other in Neighbors.openNeighborhood(graph, dominator)) {
                other.dominated = true
            }
        }
        for (v in graph.vertexSet()) {
            if (!v!!.dominated) return false
        }
        return true
    }

    internal class VertexDominated<V>(vertex: V) {
        var dominated = false
        var vertex: V? = null

        init {
            this.vertex = vertex
        }
    }

    internal class EdgeDominated {
        var dominated = false
    }

    override fun call(): Collection<V>? {
        if (graph == null || graphSize() == 0) return emptySet()
        if (graphEdgeSize() == 0) return graph.vertexSet()
        val solution: HashSet<V> = HashSet()
        val ci = ConnectivityInspector<V, E>(graph)
        for (vertices in ci.connectedSets()) {
            solution.addAll(execute(InducedSubgraph.inducedSubgraphOf(graph, vertices))!!.asIterable())
            if (cancelFlag) return null
        }
        return solution
    }
}