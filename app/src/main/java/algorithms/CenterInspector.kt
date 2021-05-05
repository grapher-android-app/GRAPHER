package algorithms

import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths
import org.jgrapht.graph.SimpleGraph
import kotlin.collections.HashMap
import kotlin.math.max

class CenterInspector {

    companion object{
       fun <V,E> getCenter(graph: SimpleGraph<V, E>):  V? {
           var center: V? = null
           var minDistance: Int = graph.vertexSet().size
           val paths = FloydWarshallShortestPaths<V, E>(graph)


           val vertexToAntiSize : Map<V, Int> = calculateComponents(graph)

           for(v: V in graph.vertexSet()){
               var currentDistance: Int = 0

               for(u: V in graph.vertexSet()){
                   if(u != v){

                       var distance: Double = paths.getPath(v, u).length.toDouble()

                       if (distance == Double.POSITIVE_INFINITY){
                           distance = vertexToAntiSize[v]?.toDouble()!!

                       }

                       currentDistance = max(currentDistance.toDouble(), distance).toInt()
                   }
               }
               if(currentDistance < minDistance){
                   minDistance = currentDistance
                   center = v
               }
           }
            return center
       }

        private fun <V,E> calculateComponents(graph: SimpleGraph<V, E>): Map<V, Int> {
            val vertexToAntiSize = HashMap<V, Int>()
            val ci = ConnectivityInspector<V, E>(graph)

            val ccs = ci.connectedSets()
            var largestComponent: Int = 0

            for(cc in ccs){
                largestComponent = max(largestComponent, cc.size)
            }

            for(i in 0 until ccs.size){
                val componentSize : Int = ccs[i].size
                for (v: V in ccs[i]){
                    vertexToAntiSize[v] = (largestComponent - componentSize)
                }
            }
            return vertexToAntiSize
        }
    }


}