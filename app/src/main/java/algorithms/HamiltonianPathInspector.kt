package algorithms

import android.util.SparseArray
import org.jgrapht.GraphPath
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.graph.GraphWalk
import org.jgrapht.graph.SimpleGraph
import util.InducedSubgraph
import util.PowersetIterator
import java.util.*

class HamiltonianPathInspector<V, E>(graph: SimpleGraph<V, E>?) : Algorithm<V, E, GraphPath<V, E>?>(graph) {
    private val isPotentiallyYesInstance: Boolean
        private get() {
            if (!ConnectivityInspector<V, E>(graph).isConnected()) return false
            if (graph==null){
                return false
            }
            val cuts: HashSet<V> = CutAndBridgeInspector.findAllCutVertices(graph)

            val vertices: MutableSet<V> = HashSet()
            vertices.addAll(graph.vertexSet())
            for (v in cuts) {
                vertices.remove(v)
                val g: SimpleGraph<V, E> = InducedSubgraph.inducedSubgraphOf(graph,
                        vertices)
                if (ConnectivityInspector(g).connectedSets().size > 2) return false
                vertices.add(v)
            }
            return true
        }

    override fun execute(): GraphPath<V, E>? {
        if (!isPotentiallyYesInstance) return null
        val n: Int = graphSize()
        val npow = Math.pow(2.0, n.toDouble()).toInt()
        val idToVertex = SparseArray<V>(n)
        val vertexToId: MutableMap<V, Int> = HashMap(n)
        val collectionToId: MutableMap<Collection<V>, Int> = HashMap(
                npow)
        val idToCollection = SparseArray<Collection<V>>(
                npow)
        val pi = graph?.let { PowersetIterator<V>(it.vertexSet()) }
        var counter = 0
        progress(0, PowersetIterator.twoPower(graphSize()))
        if (pi != null) {
            while (pi.hasNext()) {
                val set = pi.next()
                collectionToId[set] = counter
                idToCollection.put(counter, set)
                counter++
            }
        }
        if (cancelFlag) return null
        progress(1, counter)
        counter = 0
        if (graph != null) {
            for (v in graph.vertexSet()) {
                idToVertex.put(counter, v)
                vertexToId[v] = counter
                counter++
            }
        }

        /*
		 * Standard Hamiltonian path algorithm in 2^n time by dynamic
		 * programming. The table dp[v][S] is true if v is in S and there is a
		 * path going through the entire S and ending in v. Recursively we test
		 * whether dp[u][S-v] is true for some u in S.
		 */

        // base cases, let ( id(v) , id({v}) ) := true
        val dp = Array(n) { BooleanArray(npow) }
        for (i in 0 until n) {
            val v = idToVertex[i]
            val s = HashSet<V>(1)
            s.add(v)
            val setId = collectionToId[s]!!
            dp[vertexToId[v]!!][setId] = true
        }

        // s is the id for a subset currentSet
        for (s in 0 until npow) {
            if (cancelFlag) return null
            progress(s, npow)

            // currentSet is the set corresponding to 's'
            val currentSet = idToCollection[s]
            for (v in 0 until n) {
                // considering dp[currentVertex][currentSet] =? true
                val currentVertex = idToVertex[v]
                if (!currentSet.contains(currentVertex)) continue
                val newSet: MutableSet<V> = HashSet(currentSet.size)
                newSet.addAll(currentSet)
                newSet.remove(currentVertex)
                for (newVertex in newSet) {
                    if (dp[vertexToId[newVertex]!!][collectionToId[newSet]!!]) {
                        // There is a path in newSet ending in newVertex,
                        // is there an edge between newVertex an currentVertex?
                        if (graph != null) {
                            if (graph.containsEdge(currentVertex, newVertex)) {
                                // vu is an edge
                                dp[vertexToId[currentVertex]!!][collectionToId[currentSet]!!] = true
                                break
                            }
                        }
                    }
                }
            }
        }
        var pathEnds = -1
        for (i in 0 until n) {
            if (graph != null) {
                if (dp[i][collectionToId[graph.vertexSet()]!!]) {
                    pathEnds = i
                    break
                }
            }
        }
        if (pathEnds < 0) {
            return null
        }

        // we need to reconstruct path from dp table!
        val hamPath = ArrayList<V>(n)
        val edgeList = ArrayList<E>(n)
        var currentVertex = idToVertex[pathEnds]
        val currentSet: MutableCollection<V> = HashSet(n)
        if (graph != null) {
            currentSet.addAll(graph.vertexSet())
        }
        hamPath.add(currentVertex)

        // going backwards from pathEnds
        for (i in 0 until n - 1) {
            currentSet.remove(currentVertex)
            val currentSetId = collectionToId[currentSet]!!
            for (newVertexId in 0 until n) {
                if (dp[newVertexId][currentSetId]) {
                    val newVertex = idToVertex[newVertexId]
                    if (graph != null) {
                        graph.getEdge(currentVertex, newVertex)
                    }

                    // if it doesn't contain this edge, a different newVertex is
                    // witness (next in path)
                    if (graph != null) {
                        if (!graph.containsEdge(currentVertex, newVertex)) {
                            continue
                        }
                    }
                    currentVertex = newVertex
                    hamPath.add(currentVertex)
                    break
                }
            }
        }
        for (i in 1 until hamPath.size) {
            val v = hamPath[i - 1]
            val u = hamPath[i]
            var e: E = graph!!.getEdge(v, u)
            if (e == null) {
                e = graph.getEdge(u, v)
                System.err.println("Edge was null but should be none null: "
                        + e)
                System.err.println("current = $v")
                System.err.println("new     = $u")
            }
            edgeList.add(e)
        }
        return GraphWalk<V, E>(graph, hamPath[0],
                hamPath[hamPath.size - 1], edgeList, 0.0)
    }
}