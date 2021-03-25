package model

import java.util.function.Supplier

/**
 * Returns nothing atm
 */
class DefaultSupplier<V> : Supplier<V> {
    val item : V
        get() {
            return item
        }

    override fun get(): V = item

}