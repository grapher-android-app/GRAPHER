package model

import java.io.Serializable

// TODO Find replacement for EdgeFactory

class DefaultEdgeFactory<V> : Serializable {

    companion object {
        const val serialVersionUID = 1L
    }

    fun createEdge(source: V, target: V): DefaultEdge<V> {
        return DefaultEdge<V>(source, target)
    }
}