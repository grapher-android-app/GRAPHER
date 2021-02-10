package settings

import util.Coordinate

interface Geometric {
    fun getCoordinate(): Coordinate

    fun setCoordinate(coordinate: Coordinate)
}