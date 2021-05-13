package interval

import org.jgrapht.graph.SimpleGraph

class SimpleToBasicWrapper <V, E> (val sg: SimpleGraph<V, E>)  {

    private val bg = BasicGraph()

    val vertexMap = HashMap<V, Int>()
    val edgeMap = HashMap<E, Int>()

    val vertexMapBack = HashMap<Int, V>()
    val edgeMapBack = HashMap<Int, E>()

    init {
        for (v : V in sg.vertexSet()) {
            val x = bg.addVertex()
            vertexMap[v] = x
            vertexMapBack[x] = v
        }
        for (e : E in sg.edgeSet()) {
            val v : V = sg.getEdgeSource(e)
            val u : V = sg.getEdgeSource(e)
            bg.addEdge(vertexMap[v]!!, vertexMap[v]!!)
        }
    }

    //TODO add Interval GraphSupport in CliqueChain
    /*
    fun getIntervalGraph() : IntervalGraph {
        return CliqueChain.getIntervalGraph(bg)
    }
     */

    fun getAT() : HashSet<V>? {
        val at : HashSet<Int> = bg.getAT() ?: return null

        val ret = HashSet<V>(3)
        for (i : Int in at) {
            ret.add(vertexMapBack[i]!!)
        }
        return ret
    }

    fun isChordal() : Boolean {
        return bg.isChordal()
    }

    fun getVertexTranslation() : HashMap<Int, V> {
        return vertexMapBack
    }

    fun getEdgeTranslation() : HashMap<Int, E> {
        return edgeMapBack
    }
}