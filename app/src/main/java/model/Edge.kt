package model

import settings.Colorful
import settings.Geometric
import util.Coordinate
import java.io.Serializable

class Edge<V>(private val source: V, private val target: V) : Colorful, Geometric, Serializable{
    companion object {
        private const val serialVersionUID = 1L
    }
    private var color: Int = 0
    private lateinit var style: EdgeStyle

    private lateinit var coordinate: Coordinate


    override fun getColor(): Int {
        return color
    }

    override fun setColor(color: Int) {
        this.color = color
    }

    override fun getCoordinate(): Coordinate {
        return coordinate
    }

    override fun setCoordinate(coordinate: Coordinate) {
        this.coordinate = coordinate
    }

    fun getStyle(): EdgeStyle {
        return style
    }

    fun setStyle(style: EdgeStyle) {
        this.style = style
    }

    fun isIncident(vertex: V): Boolean{
        return source!!.equals(vertex) || target!!.equals(vertex)
    }

    fun getOpposite(vertex: V?): V? {
        if (source != null) {
            if (source.equals(vertex)){
                return target
            }
        }
        if (target != null) {
            if (target.equals(vertex)){
                return source
            }
        }
        return null
    }

    fun getSource(): V {
        return source
    }

    fun getTarget(): V {
        return target
    }

    override fun toString(): String {
        return "DefaultEdge: $source -- $target"
    }

}