package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.items.callables.AmountChangedCallable

class Amount(
    amount: Int = 1,
    val maxStack: Int = 1
): Attribute() {
    var amount: Int = amount
        set(value) {
            field = value
            entity.call(AmountChangedCallable::class, amount)
        }

    fun canStackMore(): Boolean {
        return maxStack > amount
    }

    fun stackRemaining(): Int {
        return maxStack - amount
    }
}