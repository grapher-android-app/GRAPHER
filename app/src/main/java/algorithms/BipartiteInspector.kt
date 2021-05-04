package algorithms

import org.jgrapht.graph.SimpleGraph

class BipartiteInspector {

    companion object{

        /**
         * Returns bipartition, one of the partitions
         *
         * @param graph
         * @return bipartition, null if not bipartite
         */
        fun <V, E> getBipartition(graph: SimpleGraph<V, E>): HashSet<V>?{
            if(graph.vertexSet().size == 0){
                return HashSet<V>()
            }

            var a  = HashSet<V>(graph.vertexSet().size)
            var b   = HashSet<V>(graph.vertexSet().size)

            var notProcessed  = HashSet<V>()
            notProcessed.addAll(graph.vertexSet())

            var fringe = HashSet<V>(graph.vertexSet().size)

            while(notProcessed.isNotEmpty()){
                var current : V = notProcessed.iterator().next()
                fringe.add(current)
                notProcessed.remove(current)

                //Current is the first in its connected component to bre processed
                a.add(current)

                while (fringe.isNotEmpty()){
                    current = fringe.iterator().next()
                    fringe.remove(current)
                    notProcessed.remove(current)

                    if(a.contains(current)){
                        for(e: E in graph.edgesOf(current)){
                            val u: V = opposite(graph, e, current)
                            if(a.contains(u)){
                                return null
                            }
                            b.add(u)
                            if(notProcessed.contains(u)){
                                fringe.add(u)
                            }
                        }
                    } else if (b.contains(current)){
                        for(e: E in graph.edgesOf(current)) {
                            val u: V = opposite(graph, e, current)
                            if(b.contains(u)) return null
                            a.add(u)
                            if(notProcessed.contains(u)){
                                fringe.add(u)
                            }
                        }
                    }
                }
            }
            return a
        }


        /**
         * Returns true iff graph is bipartite
         *
         * @param graph
         * @return true if input is bipartite
         */

        fun <V, E> isBipartite(graph: SimpleGraph<V, E>): Boolean{
            return getBipartition(graph) != null
        }




        private fun <V, E> opposite(g: SimpleGraph<V, E>, e: E, v: V) : V{
            if(g.getEdgeSource(e) == v){
                return g.getEdgeTarget(e)
            }
            return g.getEdgeSource(e)
        }
    }
}