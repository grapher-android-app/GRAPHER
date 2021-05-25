package algorithms

import algorithms.CutAndBridgeInspector
import android.util.SparseArray
import org.jgrapht.GraphPath
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.graph.GraphWalk
import org.jgrapht.graph.SimpleGraph
import util.Neighbors
import util.PowersetIterator
import java.util.*

class HamiltonianCycleInspector<V, E>(graph: SimpleGraph<V, E>?) : Algorithm<V, E, GraphPath<V, E>?>(graph) {
    private val isPotentiallyYesInstance: Boolean
        get() = CutAndBridgeInspector.findCutVertex(graph!!) == null

    override fun call(): GraphPath<V, E>? {
        if (graph != null) {
            if (graph.vertexSet().size < 3) return null
        }
        val con: Boolean = ConnectivityInspector<V, E>(graph).isConnected
        if (!con || !isPotentiallyYesInstance) return null
        val n: Int = graph!!.vertexSet().size
        val npow = Math.pow(2.0, n.toDouble()).toInt()
        val idToVertex = SparseArray<V>(n)
        val vertexToId: MutableMap<V, Int> = HashMap(n)
        val collectionToId: MutableMap<Collection<V>, Int> = HashMap(
                npow)
        val idToCollection = SparseArray<Collection<V>>(
                npow)
        val pi = PowersetIterator<V>(graph.vertexSet())
        var counter = 0
        while (pi.hasNext()) {
            val set = pi.next()
            collectionToId[set] = counter
            idToCollection.put(counter, set)
            counter++
        }
        var minDegree = n + 2
        var minDegreeVertex: V? = null
        var minDegreeVertexId = -1
        counter = 0
        for (v in graph.vertexSet()) {
            idToVertex.put(counter, v)
            vertexToId[v] = counter
            val deg: Int = graph.degreeOf(v)
            if (deg < minDegree) {
                minDegree = deg
                minDegreeVertex = v
                minDegreeVertexId = counter
            }
            counter++
        }
        println("CANCEL?!?")
        if (cancelFlag) return null
        progress(0, graphSize())

        /*
		 * Standard Hamiltonian path algorithm in 2^n time by dynamic
		 * programming. The table dp[v][S] is true if v is in S and there is a
		 * path going through the entire S and ending in v. Recursively we test
		 * whether dp[u][S-v] is true for some u in S.
		 */
        val dp = Array(n) { BooleanArray(npow) }

        // base case, let ( id(v) , id({v}) ) := true iff v = minDegreeVertex
        val minDegreeVertexSingleton = HashSet<V>(1)
        minDegreeVertexSingleton.add(minDegreeVertex!!)
        val setId = collectionToId.get(minDegreeVertexSingleton)
        dp[minDegreeVertexId][setId!!] = true

        // s is the id for a subset currentSet
        for (s in 0 until npow) {
            // currentSet is the set corresponding to 's'
            val currentSet = idToCollection[s]
            if (cancelFlag) return null
            progress(s, npow)
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
                        if (graph.containsEdge(currentVertex, newVertex)) {
                            // vu is an edge
                            dp[vertexToId[currentVertex]!!][collectionToId[currentSet]!!] = true
                            break
                        }
                    }
                }
            }
        }
        var pathEnds = -1

        // we test that there is a vertex adjacent to minDegreeVertex that has a
        // ham path ending in it
        val vertexSetId = collectionToId[graph.vertexSet()]!!
        for (nabo in Neighbors.openNeighborhood(graph, minDegreeVertex)) {
            val naboId = vertexToId[nabo]!!
            if (dp[naboId][vertexSetId]) {
                // YES! there is a ham path from minDegree to a neighbor of it!
                pathEnds = naboId
                break
            }
        }
        if (pathEnds < 0) {
            // System.out.println("No hamiltonian path");
            return null
        }

        // System.out.println("We found hamiltonian path ending in " + pathEnds
        // + " = " + idToVertex.get(pathEnds));

        // we need to reconstruct path from dp table!
        val hamPath = ArrayList<V>(n)
        val edgeList = ArrayList<E>(n)
        var currentVertex = idToVertex[pathEnds]

        // this is the edge that is not part of the path but is present since
        // pathEnds was in the neighborhood of minDegreeVertex
        edgeList.add(graph.getEdge(minDegreeVertex, currentVertex))
        val currentSet: MutableCollection<V> = HashSet(n)
        currentSet.addAll(graph.vertexSet())
        hamPath.add(currentVertex)

        // going backwards from pathEnds
        for (i in 0 until n - 1) {
            currentSet.remove(currentVertex)
            val currentSetId = collectionToId[currentSet]!!
            for (newVertexId in 0 until n) {
                if (dp[newVertexId][currentSetId]) {
                    val newVertex = idToVertex[newVertexId]
                    graph.getEdge(currentVertex, newVertex)

                    // if it doesn't contain this edge, a different newVertex is
                    // witness (next in path)
                    if (!graph.containsEdge(currentVertex, newVertex)) {
                        continue
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
            var e: E = graph.getEdge(v, u)
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