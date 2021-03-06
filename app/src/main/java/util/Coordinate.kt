package util

import java.io.Serializable
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.round
import kotlin.math.sqrt

class Coordinate(private val x: Float, private val y: Float) : Cloneable, Serializable {

    //* All static variables in the class */
    companion object {
        private const val serialVersionUID = 1L
        val ORIGO = Coordinate(0F, 0F)
        val ZERO = ORIGO
        val UNIT_X = Coordinate(1F, 0F)
        val UNIT_Y = Coordinate(0F, 1F)
    }

    constructor (c : Coordinate) : this(c.getX(), c.getY())

    override fun clone(): Coordinate {
        return Coordinate(this)
    }

    fun rounded(): Coordinate {
        return Coordinate(round(x), round(y))
    }

    fun normalize(): Coordinate {
        val length = length()
        val nx = x / length
        val ny = y / length
        return Coordinate(nx, ny)
    }

    fun angle(): Float {
        return atan2(y, x) * (180 / PI).toFloat()
    }

    fun length(): Float {
        return sqrt(x * x + y * y)
    }

    fun add(c: Coordinate): Coordinate {
        return Coordinate(x + c.x, y + c.y)
    }

    fun multiply(scalar: Float): Coordinate {
        return Coordinate(x * scalar, y * scalar)
    }

    fun subtract(c: Coordinate): Coordinate {
        return Coordinate(x - c.x, y - c.y)
    }

    fun sq(a: Float): Float {
        return a * a
    }

    fun distance(c: Coordinate): Float {
        return sqrt(sq(x - c.x) + sq(y - c.y))
    }

    fun moveVector(c: Coordinate): Coordinate {
        val cx = c.x
        val cy = c.y
        return Coordinate(cx - x, cy - y)
    }

    fun inverse(): Coordinate {
        return Coordinate(-x, -y)
    }

    fun getX(): Float {
        return x
    }

    fun getY(): Float {
        return y
    }

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (other is Coordinate)
            return (x == other.x && y == other.y)
        else {
            return false
        }
    }

    override fun toString(): String {
        return "[x=$x, y=$y]"
    }
}