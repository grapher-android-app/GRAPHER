package algorithms

import algorithms.BipartiteInspector
import org.jgrapht.graph.SimpleGraph
import util.NChooseKIterator
import util.Neighbors
import java.math.BigInteger
import java.util.*

/**
 * (in comments ISet = independent set) Class that contains methods for
 * calculating a graphs chromatic number. There is also a method for dividing a
 * graph into k ISets each of separate colour that together cover the graph.
 * Methods are based on "Inclusion-Exclusion based algorithms for graph
 * colouring" by Andreas Björklund and Thore Husfeldt.
 *
 * @author Håvard Haug
 */
class ChromaticNumber<V, E>(graph: SimpleGraph<V, E>?) : Algorithm<V, E, Int?>(graph) {
    private lateinit var fib: Array<BigInteger?>

    /**
     * Method that computes the chromatic number of a graph
     *
     * @param graph
     * @return chromatic number
     */
    fun <V, E> getChromaticNumber(graph: SimpleGraph<V, E>): Int {
        val n = graph.vertexSet().size
        setProgressGoal(n + 1)
        // no vertices can be coloured by 0 colours
        if (graph.vertexSet().isEmpty()) {
            return 0
        }

        // an ISet can be coloured by the same colour
        if (graph.edgeSet().isEmpty()) {
            return 1
        }

        // check for bipartition by other algorithm as it
        // is much faster
        if (BipartiteInspector.isBipartite(graph)) {
            return 2
        }

        // calculate lookup table for Fibonacci
        // series for use in small degree ISet counter
        fib = arrayOfNulls(n)
        fib[0] = BigInteger("1")
        fib[1] = BigInteger("2")
        for (i in 2 until n) {
            fib[i] = fib[i - 1]!!.add(fib[i - 2])
        }
        val sums = chromatic(graph)

        /*
     * find the chromatic number by binary search reducing the total runtime.
     * locates smallest k for which a colouring is possible.
     */
        var upper = n
        var lower = 3
        while (upper > lower) {
            val mid = (upper + lower) / 2
            if (sums[mid]!!.compareTo(BigInteger.ZERO) == 1) {
                upper = mid
            } else {
                lower = mid + 1
            }
        }
        increaseProgress()
        return upper
    }

    /**
     * Number of ways to cover graph with k ISets inclusion-exclusion formula from
     * previously mentioned paper.
     *
     * @param graph
     * to be covered
     * @param k
     * number of ISets
     * @return Number of ways to cover graph with k ISets
     */
    private fun <V, E> chromatic(graph: SimpleGraph<V, E>): Array<BigInteger?> {
        val n = graph.vertexSet().size
        val sums = arrayOfNulls<BigInteger>(n + 1)
        for (i in 0..n) {
            sums[i] = BigInteger("0")
        }
        val neg = BigInteger("-1")
        for (i in 0..n) {
            var nk: NChooseKIterator<V>
            nk = NChooseKIterator(graph.vertexSet(), i)
            while (nk.hasNext()) {
                val currSet = nk.next()
                var currGraph: SimpleGraph<V, E>
                currGraph = graph.clone() as SimpleGraph<V, E>
                currGraph.removeAllVertices(currSet)
                var nrISets = countISets(currGraph)
                // Remove empty set from count
                // as not legal ISet according to paper
                nrISets = nrISets.add(neg)
                for (j in 0..n) {
                    sums[j] = sums[j]!!.add(neg.pow(currSet.size).multiply(nrISets.pow(j)))
                }
            }
            increaseProgress()
        }
        return sums
    }

    /**
     * Counts the number of ISets in the graph by branching on vertices of degree
     * over 2, and using a faster method once no over 2 degree vertices remain.
     *
     * @param graph
     * @return number of ISets in the graph
     */
    private fun <V, E> countISets(newgraph: SimpleGraph<V, E>): BigInteger {
        val graph: SimpleGraph<V, E>
        graph = newgraph.clone() as SimpleGraph<V, E>
        var v: V? = null
        val tmpvset = graph.vertexSet()
        for (tmpv in tmpvset) {
            if (graph.degreeOf(tmpv) > 2) {
                v = tmpv
                break
            }
        }
        return if (v == null) {
            countSmallDegISets(graph)
        } else {
            val openNeighbourhood: Collection<V>
            openNeighbourhood = Neighbors.openNeighborhood(graph, v)
            graph.removeVertex(v)
            val ISetWithoutV = countISets(graph)
            graph.removeAllVertices(openNeighbourhood)
            val ISetWithV = countISets(graph)
            ISetWithoutV.add(ISetWithV)
        }
    }

    /**
     * Counts the number of ISets that can be formed by a graph of vertices when
     * all vertices have degree <= 2. this is done by finding the number of ISets
     * that each connected component of the graph can form, then multiplying these
     * values.
     *
     * @param newgraph
     * graph with no > 2 degree vertices
     * @return number of ISets that can be formed within the graph
     */
    private fun <V, E> countSmallDegISets(newgraph: SimpleGraph<V, E>): BigInteger {
        val graph: SimpleGraph<V, E>
        graph = newgraph.clone() as SimpleGraph<V, E>
        var remaining: Set<V> = HashSet(graph.vertexSet())
        var isets = BigInteger("1")
        // Discover degree 0 vertices
        for (v in remaining) {
            if (graph.degreeOf(v) == 0) {
                isets = isets.multiply(fib[1])
                graph.removeVertex(v)
            }
        }
        remaining = HashSet(graph.vertexSet())
        // Discover paths
        var used: MutableSet<V> = HashSet()
        for (v in remaining) {
            if (used.contains(v)) {
                continue
            }
            if (graph.degreeOf(v) == 1) {
                var counter = 1
                var currV = v
                var preV = v
                used.add(currV)
                var edgeSet: MutableSet<E> = HashSet(graph.edgesOf(currV))
                do {
                    counter++
                    for (tmpe in edgeSet) {
                        val v1 = Neighbors.opposite(graph, currV, tmpe)
                        if (v1 != preV) {
                            preV = currV
                            currV = v1
                            break
                        }
                    }
                    edgeSet = HashSet(graph.edgesOf(currV))
                    edgeSet.remove(graph.getEdge(currV, preV))
                    used.add(currV)
                } while (!edgeSet.isEmpty())
                isets = isets.multiply(fib[counter])
            }
        }
        // Remove all vertices in paths
        for (v in used) {
            graph.removeVertex(v)
        }
        remaining = HashSet(graph.vertexSet())
        used = HashSet()

        // Discover cycles
        for (tmpv in remaining) {
            if (used.contains(tmpv)) {
                continue
            }
            var counter = 0
            var currV = tmpv
            var preV = tmpv
            used.add(currV)
            var edgeSet: MutableSet<E> = HashSet(graph.edgesOf(currV))
            while (!(counter > 0 && currV == tmpv)) {
                counter++
                for (tmpe in edgeSet) {
                    val v1 = Neighbors.opposite(graph, currV, tmpe)
                    if (v1 != preV) {
                        preV = currV
                        currV = v1
                        break
                    }
                }
                edgeSet = HashSet(graph.edgesOf(currV))
                edgeSet.remove(graph.getEdge(currV, preV))
                used.add(currV)
            }
            isets = isets.multiply(fib[counter - 1]!!.add(fib[counter - 3]))
        }
        return isets
    }

    override fun call(): Int? {
        if (graph==null) return null
        return getChromaticNumber(graph)
    }
}