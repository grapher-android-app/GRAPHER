package model

import util.Coordinate

import java.io.Serializable

import android.graphics.Color
import settings.Colorful
import settings.Geometric
import settings.Labelled
import settings.Sized

class DefaultVertex : Colorful, Geometric, Labelled, Sized, Serializable {
    companion object {
        private const val serialVersionUID = 1L
        private var CURRENT_ID = 1
        val DEFAULT_SIZE = 15F

        //TODO find replacement for synchronized
        fun resetCounter() {
            CURRENT_ID = 1
        }
    }

    /** lateinit means it's intialized in the constructor */
    private var color: Int = 0
    private lateinit var coordinate: Coordinate
    private lateinit var label: String
    private var size: Float = 0F

    //changed from being done in constructor, does not seem to be a difference
    private val id: Int = CURRENT_ID++

    constructor (coordinate: Coordinate) {
        DefaultVertex(Color.rgb(0, 0, 200), coordinate, DEFAULT_SIZE)
    }

    constructor (color: Int, coordinate: Coordinate, size: Float) {
        DefaultVertex(color, coordinate, size, "")
    }

    constructor (color: Int, coordinate: Coordinate, size: Float, label: String) {
        this.color = color
        this.coordinate = coordinate
        this.size = size
        this.label = label
    }

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

    fun getId(): Int {
        return id
    }

    override fun getLabel(): String {
        return label
    }

    override fun setLabel(label: String) {
        this.label = label
    }

    override fun getSize(): Float {
        return size
    }
    override fun setSize(size: Float) {
        this.size = size
    }

    override fun toString(): String {
        return "dv$id"
    }
}