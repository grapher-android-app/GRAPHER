package algorithms

import org.jgrapht.GraphPath
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.SimpleGraph

class DiameterInspector {

    companion object{

        fun <V, E> diameter(g: SimpleGraph<V, E>): Int {
           val diamPath: GraphPath<V, E> = diameterPath(g) ?: return -1
            return diamPath.edgeList.size
        }


        fun <V, E> diameterPath(g: SimpleGraph<V, E>) : GraphPath<V, E>?{
            var d: DijkstraShortestPath<V, E>
            val ci = ConnectivityInspector<V, E>(g)
            if(!ci.isConnected) {
                return null
            }

            var longestPath : GraphPath<V, E>? = null
            for(v: V in g.vertexSet()){
                for(u: V in g.vertexSet()){
                    if(v != u){
                        //d = DijkstraShortestPath<V,E>(g,u,v)
                        d = DijkstraShortestPath<V,E>(g) //changed to this, idk if that okay
                        //val currentPath: GraphPath<V,E>? = d.getPath()
                        val currentPath: GraphPath<V,E>? = d.getPath(v,u) //idk med dette heller
                        if (currentPath != null) {
                            if(longestPath == null || longestPath.edgeList.size < currentPath.edgeList.size){
                                longestPath = currentPath
                            }
                        }
                    }
                }
            }
            return longestPath
        }

    }
}