package algorithms

import org.jgrapht.graph.SimpleGraph

class EulerianInspector {


    companion object{
        fun <V, E> isEulerian(g: SimpleGraph<V, E>?): Boolean{
            if(g==null){
                println(g!!) //this will throw nullpointerexception if null
            }

            for(v: V in g.vertexSet()){
                if(g.edgesOf(v).size % 2 != 0){
                    return false
                }
            }
            return true
        }

        fun <V, E> getOddDegreeVertices(g: SimpleGraph<V, E>?): HashSet<V> {
            if(g==null){
                println(g!!) //this will throw nullpointerexception if null
            }
            var odds  = HashSet<V>(g.vertexSet().size)

            for(v: V in g.vertexSet()){
                if(g.edgesOf(v).size % 2 != 0){
                    odds.add(v)
                }
            }
            return odds
        }
    }
}