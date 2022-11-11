package com.neutrino.game.domain.model.entities.utility

import com.neutrino.game.domain.model.items.Item

interface Container {
    val itemList: MutableList<Item>
    val itemTiers: List<Int>
    val generationProbability: Float
    fun dropItems(): MutableList<Item> {
        return itemList
    }
}