package com.neutrino.game.domain.model.entities.utility

import com.neutrino.game.domain.model.items.Item

interface Container {
    val itemList: MutableList<Item>
    /** @param first: item tier
     * @param second: probability of being filled */
    val itemTiers: List<Pair<Int, Float>>
    fun dropItems(): MutableList<Item> {
        return itemList
    }
}