package model

import util.Coordinate

import java.io.Serializable

import android.graphics.Color

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

    fun getColor(): Int {
        return color
    }

    fun setColor(color: Int) {
        this.color = color
    }

    fun getCoordinate(): Coordinate {
        return coordinate
    }

    fun setCoordinate(coordinate: Coordinate) {
        this.coordinate = coordinate
    }

    fun getId(): Int {
        return id
    }

    fun getLabel(): String {
        return label
    }

    fun setLabel(label: String) {
        this.label = label
    }

    fun getSize(): Float {
        return size
    }
    fun setSize(size: Float) {
        this.size = size
    }

    override fun toString(): String {
        return "dv$id"
    }
}