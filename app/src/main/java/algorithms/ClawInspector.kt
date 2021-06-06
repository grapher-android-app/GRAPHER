package algorithms

import android.util.Log
import org.jgrapht.graph.SimpleGraph
import util.Neighbors

class ClawInspector {

    companion object{
         fun <V,E> findClaw(graph: SimpleGraph<V, E>) : Claw<V, E>?{
            var vertices: ArrayList<V> = ArrayList(graph.vertexSet().size)
            vertices.addAll(graph.vertexSet())

            Log.d("vertices", "$vertices uh oh")
            val n: Int = vertices.size
            if(n < 4){
                return null
            }

            for(i in 0 until n){
                val v: V = vertices[i]
                val Nv = Neighbors.openNeighborhood(graph,v)

                //THIS IS CENTER VERTEX, vertices[j] = u
                for(u: V in Nv){
                    if(u == v){
                        continue
                    }
                    //u is in the center of a star which v is an arm?
                    val Nu = Neighbors.openNeighborhood(graph,u)
                    Nu.removeAll(Nv)
                    Nu.remove(v)


                    //In Nu - Nv
                    for(x: V in Nu){
                        for(y: V in Nu){
                            if(x == y  || graph.containsEdge(x,y)) continue
                            return Claw(graph,u,v,x,y)

                        }

                    }
                }
            }
            return null
        }

        fun <V,E> minimalClawDeletionSet(graph: SimpleGraph<V,E>) : Collection<E>?{
            if(getClaws(graph).getCenters().isEmpty()) return HashSet<E>()
            val copy: SimpleGraph<V, E> = graph
            val m: Int = copy.edgeSet().size
            for(i in 1 until m){
                val solution = minimalClawDeletionSet(copy, i)
                if(solution != null) return solution
            }
            System.err.println("Should not come here! did not find solution set")
            return null
        }

        private fun <V, E> minimalClawDeletionSet(graph: SimpleGraph<V, E>, k: Int): Collection<E>?{
            val claw: Claw<V, E> = findClaw(graph) ?: return HashSet<E>()

            if(k <= 0) return null

            for(edge: E in claw.getEdges()) {
                val v: V = graph.getEdgeSource(edge)
                val u: V = graph.getEdgeTarget(edge)

                graph.removeEdge(v,u)
                var res: Collection<E>? = minimalClawDeletionSet(graph, k-1)
                graph.addEdge(v,u,edge)

                if(res != null){

                    res = res.toHashSet()
                    res.add(edge)
                    return res
                }


            }
            return null
        }

        fun <V,E> getClaws(graph: SimpleGraph<V,E>): ClawCollection<V>{
            var col: ClawCollection<V> = ClawCollection<V>()

            var vertices: ArrayList<V> = ArrayList<V>(graph.vertexSet().size)
            vertices.addAll(graph.vertexSet())
            val n: Int = vertices.size
            if(n < 4){return col}

            for(i: Int in 0 until n){
                val v: V = vertices[i]
                var Nv: Collection<V> =Neighbors.openNeighborhood(graph,v)

                //THIS IS CENTER VERTEX, vertices[j]=u
                for (j: Int in 0 until n){
                    if (j==i) {continue}

                    val u : V = vertices[j]
                    if (!Nv.contains(u)){ continue }
                    //u is the center of a star in which v is an arm?
                    var Nu: HashSet<V> = Neighbors.openNeighborhood(graph,u)
                    Nu.removeAll(Nv);
                    Nu.remove(v);

                    for (x: V in Nu) {
                        for (y: V in Nu) {
                        if (x == y)
                            continue;
                        if (graph.containsEdge(x, y))
                            continue;
                        col.addClaw(graph, u, v, x, y);
                    }
                    }

                }
            }
            return col;
        }

        class ClawCollection<U> {
            val centers : HashSet<U> =  HashSet<U>();
            var  arms : HashSet<Pair<U, U>> = HashSet<Pair<U, U>>();
            //should be SimpleGraph<U, ?> but uses  SimpleGraph<V, E> for now
            fun <V, E>  addClaw(graph: SimpleGraph<V, E>, center: U, v1: U, v2: U, v3: U) : Boolean {
                centers.add(center);
                arms.add( Pair(center, v1));
                arms.add( Pair(center, v2));
                arms.add( Pair(center, v3));

                return true;
            }

            fun  getCenters() : Collection<U> {
                return centers;
            }

        }

        class Claw<X, Y>(val graph: SimpleGraph<X, Y>, center: X, v1: X, v2: X, v3: X) {
            val center: X?
            val v1: X?
            val v2: X?
            val v3: X?
            private val edges: MutableCollection<Y>
            fun getEdges(): Collection<Y> {
                return edges
            }

            override fun hashCode(): Int {
                val prime = 31
                var result = 1
                result = (prime * result
                        + (center?.hashCode() ?: 0))
                result = prime * result + (v1?.hashCode() ?: 0)
                result = prime * result + (v2?.hashCode() ?: 0)
                result = prime * result + (v3?.hashCode() ?: 0)
                return result
            }

            override fun equals(obj: Any?): Boolean {
                if (this === obj) return true
                if (obj == null) return false
                if (javaClass != obj.javaClass) return false
                val other = obj as Claw<*, *>
                if (center == null) {
                    if (other.center != null) return false
                } else if (center != other.center) return false
                if (v1 == null) {
                    if (other.v1 != null) return false
                } else if (v1 != other.v1) return false
                if (v2 == null) {
                    if (other.v2 != null) return false
                } else if (v2 != other.v2) return false
                if (v3 == null) {
                    if (other.v3 != null) return false
                } else if (v3 != other.v3) return false
                return true
            }

            override fun toString(): String {
                return "Claw $edges"
            }

            init {
                this.center = center
                this.v1 = v1
                this.v2 = v2
                this.v3 = v3
                edges = HashSet(3)
                edges.add(graph.getEdge(center, v1))
                edges.add(graph.getEdge(center, v2))
                edges.add(graph.getEdge(center, v3))
            }
        }
    }

}

