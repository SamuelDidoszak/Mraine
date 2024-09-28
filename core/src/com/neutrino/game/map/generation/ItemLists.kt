package com.neutrino.game.map.generation

import com.neutrino.game.util.EntityName
import com.neutrino.game.utility.Probability

object ItemLists {

    private val itemListMap: HashMap<String, List<Probability<EntityName>>> = HashMap()

    fun add(name: String, itemList: List<Pair<Float, String>>): List<Probability<EntityName>> {
        itemListMap[name] = itemList.map { Probability(it) }
        return itemListMap[name]!!
    }

    fun get(name: String): List<Probability<EntityName>> {
        return itemListMap[name]!!
    }

    fun getAllitemLists(): HashMap<String, List<Probability<EntityName>>> {
        return itemListMap
    }

    fun getAllitemListNames(): Set<String> {
        return itemListMap.keys
    }
}
