package algorithms

import org.jgrapht.GraphPath
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.graph.GraphWalk
import org.jgrapht.graph.SimpleGraph
import util.PermutationIterator
import util.PowersetIterator
import java.util.*
import kotlin.collections.HashMap

object HamiltonianInspector {
    fun <V, E> getHamiltonianPath(
            graph: SimpleGraph<V?, E>): GraphPath<V, E>? {
        val con: Boolean = ConnectivityInspector(graph).isConnected
        if (!con) return null
        val n = graph.vertexSet().size
        val npow = Math.pow(2.0, n.toDouble()).toInt()
        val idToVertex: MutableMap<Int, V?> = HashMap(n)
        val vertexToId: MutableMap<V?, Int> = HashMap(n)
        val collectionToId: MutableMap<Collection<V?>, Int> = HashMap(
                npow)
        val idToCollection: HashMap<Int, Collection<V?>> = HashMap(
                npow)
        val pi = PowersetIterator(graph.vertexSet())
        var counter = 0
        while (pi.hasNext()) {
            val set = pi.next()
            collectionToId[set] = counter
            idToCollection[counter] = set
            counter++
        }
        counter = 0
        for (v in graph.vertexSet()) {
            idToVertex[counter] = v
            vertexToId[v] = counter
            counter++
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
            val s = HashSet<V?>(1)
            s.add(v)
            val setId = collectionToId[s]!!
            dp[vertexToId[v]!!][setId] = true
        }

        // s is the id for a subset currentSet
        for (s in 0 until npow) {
            // currentSet is the set corresponding to 's'
            val currentSet: Collection<V?>? = idToCollection[s]
            for (v in 0 until n) {
                // considering dp[currentVertex][currentSet] =? true
                val currentVertex = idToVertex[v]
                if (!currentSet!!.contains(currentVertex)) continue
                val newSet: HashSet<V?> = HashSet(currentSet.size)
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

        // for (int x = 0; x < dp.length; x++) {
        // for (int y = 0; y < dp[x].length; y++) {
        // int out = dp[x][y] ? 1 : 0;
        // System.out.print(out);
        // }
        // System.out.println();
        // }
        // System.out.println("\n======\n\n");
        var pathEnds = -1
        for (i in 0 until n) {
            if (dp[i][collectionToId[graph.vertexSet()]!!]) {
                pathEnds = i
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
        val hamPath = ArrayList<V?>(n)
        val edgeList = ArrayList<E?>(n)
        var currentVertex = idToVertex[pathEnds]
        val currentSet: HashSet<V?> = HashSet(n)
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
            var e = graph.getEdge(v, u)
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

    fun <V, E> bruteForceHamiltonianPath(
            graph: SimpleGraph<V, E>?): GraphPath<V, E>? {
        if (graph == null) throw NullPointerException("Input graph was null.")
        if (graph.vertexSet().size < 2) return null
        val con: Boolean = ConnectivityInspector(graph).isConnected
        if (!con) return null
        val pit = PermutationIterator(
                graph.vertexSet())
        while (pit.hasNext()) {
            val pi = pit.next()
            var isPath = true
            for (i in 1 until pi.size) {
                if (!graph.containsEdge(pi[i - 1], pi[i])) {
                    isPath = false
                    break
                }
            }
            if (isPath) {
                val edges = ArrayList<E>(pi.size)
                for (i in 1 until pi.size) {
                    edges.add(graph.getEdge(pi[i - 1], pi[i]))
                }
                return GraphWalk<V, E>(graph,
                        pi[0], pi[pi.size - 1], edges,0.0)
            }
        }
        return null
    }
}