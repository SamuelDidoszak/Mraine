package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.entities.util.Equality

data class ItemTier(
    val tier: Int,
    val difficulty: Int
): Attribute(), Equality<ItemTier>, Cloneable<ItemTier> {

    override fun isEqual(other: ItemTier): Boolean = tier == other.tier && difficulty == other.difficulty
    override fun clone(): ItemTier = ItemTier(tier, difficulty)
}