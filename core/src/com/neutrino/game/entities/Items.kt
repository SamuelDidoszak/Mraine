package com.neutrino.game.entities

import com.neutrino.game.entities.items.Item
import com.neutrino.game.entities.items.attributes.ItemData
import com.neutrino.game.entities.items.attributes.tags.ItemTag

object Items {
    private val itemIds: HashMap<String, Int> = HashMap()
    private val itemNames: ArrayList<String> = ArrayList()
    private val itemFactory: MutableList<() -> Entity> = mutableListOf()
    private val itemDataList: ArrayList<ItemData> = ArrayList()

    fun add(name: String, tier: Int, vararg tags: ItemTag, item: () -> Entity) {
        itemIds[name] = itemFactory.size
        itemNames.add(name)
        itemFactory.add(item)
        itemDataList.add(ItemData(tier, tags.asList()))
    }

    fun new(name: String): Item {
        try {
            return new(itemIds[name]!!) as Item
        } catch (e: Exception) {
            throw e
//            throw Exception("item with name: $name does not exist!")
        }
    }

    fun new(id: Int): Item {
//        try {
            val item = itemFactory[id].invoke()
            item.id = id
            return item as Item
//        } catch (_: Exception) {
//            System.err.println("item with id: $id ${if (id < itemNames.size) "name: ${itemNames[id]} " else ""}does not exist!")
//        }
//        throw Exception()
    }

    fun getId(name: String): Int {
        try {
            return itemIds[name]!!
        } catch (_: Exception) {
            Exception("Item with name: $name does not exist!").toString()
        }
        throw Exception()
    }

    fun getName(id: Int): String {
        try {
            return itemNames[id]
        } catch (_: Exception) {
            println("Item with id: $id ${if (id < itemNames.size) "name: ${itemNames[id]} " else ""}does not exist!")
        }
        throw Exception()
    }

    fun getData(name: String): ItemData {
        try {
            return itemDataList[itemIds[name]!!]
        } catch (_: Exception) {
            Exception("Item with name: $name does not exist!").toString()
        }
        throw Exception()
    }

    fun getAllItemData(): List<Pair<String, ItemData>> {
        return itemNames.mapIndexed { index, name -> Pair(name, itemDataList[index]) }
    }
}