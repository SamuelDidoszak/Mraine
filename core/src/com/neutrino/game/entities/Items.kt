package com.neutrino.game.entities

object Items {
    private val itemIds: HashMap<String, Int> = HashMap()
    private val itemNames: ArrayList<String> = ArrayList()
    private val itemFactory: MutableList<() -> Entity> = mutableListOf()

    fun add(name: String, item: () -> Entity) {
        itemIds[name] = itemFactory.size
        itemNames.add(name)
        itemFactory.add(item)
    }

    fun new(name: String): Entity {
//        try {
            return new(itemIds[name]!!)
//        } catch (_: Exception) {
//            System.err.println("item with name: $name does not exist!")
//        }
//        throw Exception()
    }

    fun new(id: Int): Entity {
//        try {
            val item = itemFactory[id].invoke()
            item.id = id
            return item
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
}