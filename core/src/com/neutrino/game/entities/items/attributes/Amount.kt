package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.AttributeOperations
import com.neutrino.game.entities.items.callables.AmountChangedCallable
import kotlin.math.max
import kotlin.math.min

class Amount(
    amount: Int = 1,
    val maxStack: Int = 1
): Attribute(), AttributeOperations<Amount> {
    var amount: Int = amount
        set(value) {
            field = value
            entity.call(AmountChangedCallable::class, amount)
            val gay: Attribute = clone()
        }

    fun canStackMore(): Boolean {
        return maxStack > amount
    }

    fun stackRemaining(): Int {
        return maxStack - amount
    }

    override fun plus(other: Amount): Amount = Amount(amount + other.amount, max(maxStack, other.maxStack))
    override fun minus(other: Amount): Amount = Amount(amount - other.amount, min(maxStack, other.maxStack))
    override fun clone(): Amount = Amount(amount, maxStack)
    override fun isEqual(other: Amount): Boolean = amount == other.amount && maxStack == other.maxStack
}