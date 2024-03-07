package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.items.callables.AmountChangedCallable
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.entities.util.Equality

class Amount(
    amount: Int = 1,
    val maxStack: Int = 1
): Attribute(), Equality<Amount>, Cloneable<Amount> {
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

    override fun clone(): Amount = Amount(amount, maxStack)
    override fun isEqual(other: Amount): Boolean = amount == other.amount && maxStack == other.maxStack
}