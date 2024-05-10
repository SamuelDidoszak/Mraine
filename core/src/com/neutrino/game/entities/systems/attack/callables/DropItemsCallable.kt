package com.neutrino.game.entities.systems.attack.callables

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.Inventory
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.map.chunk.ChunkManager

class DropItemsCallable: EntityDiedCallable() {
    override fun call(entity: Entity, vararg data: Any?) {
        val items = entity.get(Inventory::class)?.getAll { true }
        if (items.isNullOrEmpty())
            return
        ChunkManager.addEntityAt(entity.get(Position::class)!!, items)
    }
}