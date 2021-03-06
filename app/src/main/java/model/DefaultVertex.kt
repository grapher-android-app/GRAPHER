package model

import util.Coordinate

import java.io.Serializable

import android.graphics.Color
import settings.Colorful
import settings.Geometric
import settings.Labelled
import settings.Sized

class DefaultVertex (color: Int, coordinate: Coordinate, size: Float, label: String)
    : Colorful, Geometric, Labelled, Sized, Serializable {
    companion object {
        private const val serialVersionUID = 1L
        private var CURRENT_ID = 1
        const val DEFAULT_SIZE = 15F

        //TODO find replacement for synchronized
        fun resetCounter() {
            CURRENT_ID = 1
        }
    }

    /** lateinit means it's intialized in the constructor */
    private var color: Int
    private var coordinate: Coordinate
    private var label: String
    private var size: Float

    init {
        this.color = color
        this.coordinate = coordinate
        this.label = label
        this.size = size
    }

    //changed from being done in constructor, does not seem to be a difference
    private val id: Int = CURRENT_ID++

    constructor (coordinate: Coordinate) :
            this(Color.rgb(0, 0, 200), coordinate, DEFAULT_SIZE)

    constructor (color: Int, coordinate: Coordinate, size: Float) :
                this(color, coordinate, size, "")

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