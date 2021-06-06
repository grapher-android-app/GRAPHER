package model

import algorithms.SpringLayout
import util.Coordinate
import java.util.function.Supplier

class SpringSupplier: Supplier<SpringNode> {
    var id = 0
    override fun get(): SpringNode {
        return SpringNode(Node(Coordinate.ORIGO),id++)
    }
}