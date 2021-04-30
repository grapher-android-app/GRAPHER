package model

import settings.Geometric
import util.Coordinate

class SpringNode(val node : Geometric, val component : Int) : Geometric {
    var position : Coordinate = node.getCoordinate()
    var netForce = Coordinate.ZERO

    override fun getCoordinate(): Coordinate {
        return position
    }

    override fun setCoordinate(coordinate: Coordinate) {
        this.position = coordinate
    }

    fun sameComponent(other : SpringNode) : Boolean {
        return component == other.component
    }
}