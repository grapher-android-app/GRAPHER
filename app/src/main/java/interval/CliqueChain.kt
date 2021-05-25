package interval

import util.SetUtils
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 * Kotlin objects are the same as a Java static class
 */
object CliqueChain {

    private fun getCliqueChain
            (tree : HashMap<HashSet<Int>, HashSet<Int>>, graph : BasicGraph, peo : List<Int>)
            : ArrayList<HashSet<Int>> {

        // can't check if tree.containsKey(null) in Kotlin
        val n : Int = graph.order()
        val lst = ArrayList<HashSet<HashSet<Int>>>()

        val maximalCliques = HashSet<HashSet<Int>>(tree.size)
        maximalCliques.addAll(tree.keys)

        lst.add(maximalCliques)
        val pivots = Stack<Int>()
        val processed = HashSet<Int>(n)
        var caligraphicC = HashSet<HashSet<Int>>(n)

        // computing clique chain
        var xc : HashSet<HashSet<Int>>? = getNonSingleton(lst)

        // while xc is a non-singleton in list
        while (xc != null) {
            /*
            If pivots are empty, refine xc according to lex-last clique
             */
            if (pivots.isEmpty()) {
                // let cl be last clique in xc processed by LEX-BFS
                val cl : HashSet<Int> = getLastDiscovered(xc, peo, graph)
                        ?: throw NullPointerException("cl was null in getCliqueChain for lst = $lst")

                // replace xc by ...
                val xci : Int = lst.indexOf(xc)
                xc.remove(cl)
                val clSet = HashSet<HashSet<Int>>()
                clSet.add(cl)
                lst.add(xci + 1, clSet)
                // done replace xc by xc+cl, cl in lst

                caligraphicC = HashSet()
                caligraphicC.add(cl)
            }
            /*
            Pivots are non-empty, so we don't use xc but find xa and xb tor efine
             */
            else {
                // pic an unprocessed vertex x in pivots (throw away processed ones)
                val x = pivots.pop()
                if (processed.contains(x)) continue
                processed.add(x)

                // let C (cal C) be the set of all maximal cliques containing x
                caligraphicC = HashSet()
                for (cx : HashSet<Int> in maximalCliques) {
                    if (cx.contains(x)) caligraphicC.add(cx)
                }

                // first containing x
                var xaIndex = 0
                var xa = HashSet<HashSet<Int>>()
                for (i in 0..lst.size) {
                    if (SetUtils.containsSetContaining(lst[i], x)) {
                        xa = lst[i]
                        xaIndex = i
                        break
                    }
                }

                // first containing x
                var xbIndex = lst.size - 1
                var xb = HashSet<HashSet<Int>>()
                for (i in 0..lst.size) {
                    if (SetUtils.containsSetContaining(lst[i], x)) {
                        xb = lst[i]
                        xbIndex = i
                        break
                    }
                }

                /*
                Doing B first so the indices for A do not change when i added in lst
                 */
                val xbLeft = HashSet<HashSet<Int>>()
                val xbRight = HashSet<HashSet<Int>>()
                for (set : HashSet<Int> in xb) {
                    if (caligraphicC.contains(set)) {
                        xbLeft.add(set)
                    }
                    else {
                        xbRight.add(set)
                    }
                }
                lst.add(xbIndex + 1, xbRight)
                lst.add(xbIndex + 1, xbLeft)
                lst.removeAt(xbIndex)

                /*
                Doing A unless A = B, which means there's only one maximal clique
                 */
                if (xaIndex != xbIndex) {
                    val xaLeft = HashSet<HashSet<Int>>()
                    val xaRight = HashSet<HashSet<Int>>()
                    for (set : HashSet<Int> in xa) {
                        if (caligraphicC.contains(set)) {
                            xaLeft.add(set)
                        }
                        else {
                            xaRight.add(set)
                        }
                    }
                    lst.add(xaIndex + 1, xaRight)
                    lst.add(xaIndex + 1, xaLeft)
                    lst.removeAt(xaIndex)
                }
            }

            /*
            End of pivot/non-pivot calculations
             */

            /*
            For each tree edge connecting a bag in calC to a bag not in calC
             */
            val deleteLinks = HashSet<HashSet<Int>>()
            for (link : MutableMap.MutableEntry<HashSet<Int>, HashSet<Int>> in tree.entries) {
                val ci : HashSet<Int> = link.key // ci is child
                val cj : HashSet<Int> = link.value // cj is parent

                if (caligraphicC.contains(ci) && !caligraphicC.contains(cj)) {
                    pivots.addAll(SetUtils.intersection(ci, cj))
                    deleteLinks.add(ci)
                }
            }

            //TODO  was originaly set to null, but how to fix in Kotlin?
            for (c : HashSet<Int> in deleteLinks) tree[c] = HashSet()

            // cleaning lst
            for (i in lst.size - 1 downTo 0) {
                // lst[i] == null check in original app
                if (lst[i].size == 0) lst.removeAt(i)
            }
        }

        // now, chain contains only singletons!
        val result = ArrayList<HashSet<Int>>()
        for (i in 0..lst.size) {
            result.add(lst[i].iterator().next())
        }
        return result
    }

    /**
     * Checks for a given chain of cliques that every node appears consecutively
     * @param chain
     * @param g the BasicGraph to check nodes of
     * return true if consecutively, false if not
     */
    private fun isIntervalChain(chain : ArrayList<HashSet<Int>>, g : BasicGraph): Boolean {
        //mode 0 is before, mode 1 is in, mode 2 is after
        for (v : Int in g.getVertices()) {
            var mode = 0 // before
            for (i in 0..chain.size) {
                if (chain[i].contains(v)) {
                    if (mode == 2) return false
                    mode = 1
                }
                else {
                    // v is no longer in a clique
                    if (mode != 0) mode = 2
                }
            }
        }
        return true
    }

    private fun getLastDiscovered(cliques : Collection<HashSet<Int>>, peo : List<Int>,
                                  graph : BasicGraph) : HashSet<Int>? {
        if (cliques.isEmpty()) return null

        for (i in 0..peo.size) {
            val rnv : HashSet<Int> = rightNeighborhood(peo[i], peo, graph)
            if (cliques.contains(rnv)) return rnv
        }
        return null
    }

    private fun rightNeighborhood(vertex : Int, peo : List<Int>, graph : BasicGraph)
            : HashSet<Int> {
        val rnv : HashSet<Int> = graph.getClosedNeighborhood(vertex)
        var i = 0
        while (peo[i] != vertex) {
            rnv.remove(peo[i])
            i++
        }
        return rnv
    }

    /**
     * Return a non-singleton, i.e. a set with size > 1 if such exists, else null
     *
     * @param lst list of sets to traverse
     * @return the first non-singleton set it finds or null if none
     */
    private fun getNonSingleton(lst : ArrayList<HashSet<HashSet<Int>>>) : HashSet<HashSet<Int>>? {
        for (c : HashSet<HashSet<Int>> in lst) {
            if (c.size > 1) return c
        }
        return null
    }

    /*
    fun getIntervalGraph(g : BasicGraph) : IntervalGraph {
        //TODO add IntervalGraph support
        return
    }
    */
}