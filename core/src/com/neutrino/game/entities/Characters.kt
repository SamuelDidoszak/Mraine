package com.neutrino.game.entities

object Characters {
    private val characterIds: HashMap<String, Int> = HashMap()
    private val characterNames: ArrayList<String> = ArrayList()
    private val characterFactory: MutableList<() -> Entity> = mutableListOf()

    fun add(name: String, entity: () -> Entity) {
        characterIds[name] = characterFactory.size
        characterNames.add(name)
        characterFactory.add(entity)
    }

    fun new(name: String): Entity {
        try {
            return new(characterIds[name]!!)
        } catch (_: Exception) {
            System.err.println("Character with name: $name does not exist!")
        }
        throw Exception()
    }

    fun new(id: Int): Entity {
        try {
            val entity = characterFactory[id].invoke()
            entity.id = id
            return entity
        } catch (_: Exception) {
            System.err.println("Character with id: $id ${if (id < characterNames.size) "name: ${characterNames[id]} " else ""}does not exist!")
        }
        throw Exception()
    }
}