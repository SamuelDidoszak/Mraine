package com.neutrino.game.map.chunk

import com.neutrino.game.entities.Entity

class EntityList(
    private val onEntityChanged: (entity: Entity) -> Unit = {}
): ArrayList<Entity>() {

    override fun set(index: Int, element: Entity): Entity {
        val oldElement = super.set(index, element)
        onEntityChanged.invoke(element)
        return oldElement
    }

    override fun add(element: Entity): Boolean {
        val added = super.add(element)
        onEntityChanged.invoke(element)
        return added
    }

    override fun add(index: Int, element: Entity) {
        super.add(index, element)
        onEntityChanged.invoke(element)
    }

    override fun remove(element: Entity): Boolean {
        val oldElement = super.remove(element)
        onEntityChanged.invoke(element)
        return oldElement
    }

    override fun removeAt(index: Int): Entity {
        val oldElement = super.removeAt(index)
        onEntityChanged.invoke(oldElement)
        return oldElement
    }
}