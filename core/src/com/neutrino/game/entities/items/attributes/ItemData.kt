package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.items.attributes.tags.ItemTag
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.entities.util.Equality

data class ItemData(
    val tier: Int,
    val tagList: List<ItemTag>
): Attribute(), Equality<ItemData>, Cloneable<ItemData> {

    override fun isEqual(other: ItemData): Boolean = tier == other.tier && tagList.containsAll(other.tagList) && other.tagList.containsAll(tagList)
    override fun clone(): ItemData = ItemData(tier, tagList)
}