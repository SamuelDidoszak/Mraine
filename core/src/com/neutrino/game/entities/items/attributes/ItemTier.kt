package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.AttributeOperations

data class ItemTier(
    val tier: Int
): Attribute(), AttributeOperations<ItemTier> {
    override fun plus(other: ItemTier): ItemTier = plusPrevious(other)
    override fun minus(other: ItemTier): ItemTier = minusPrevious(other)
    override fun isEqual(other: ItemTier): Boolean = tier == other.tier
    override fun clone(): ItemTier = copy()
}