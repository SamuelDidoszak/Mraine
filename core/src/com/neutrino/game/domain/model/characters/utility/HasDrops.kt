package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.domain.model.items.Item
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

interface HasDrops {

    /** List of item drops */
    val possibleItemDropList: List<Pair<KClass<out Item>, Double>>
    /** Actual list of items to drop */
    val itemDropList: MutableList<Item>

    fun generateDropList(randomGenerator: Random) {
        possibleItemDropList.forEach {
            if (randomGenerator.nextDouble() < it.second)
                itemDropList.add(it.first.createInstance())
        }
    }

    fun dropItems(): MutableList<Item> {
        return itemDropList
    }
}