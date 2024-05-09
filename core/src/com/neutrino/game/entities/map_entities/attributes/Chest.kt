package com.neutrino.game.entities.map_entities.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.characters.attributes.Inventory
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map_entities.util.Interactable
import com.neutrino.game.map.chunk.ChunkManager

class Chest: Attribute(), Interactable {

    override var requiredDistance: Int = 1
    override var turnCost: Double = 1.0
    override var isPrimary: Boolean = true

    override fun interact() {
        ChunkManager.getEntitiesAt(entity.get(Position::class)!!).remove(entity)
        if (entity has Inventory::class) {
            ChunkManager.addEntityAt(
                entity.get(Position::class)!!,
                entity.get(Inventory::class)!!.getAll { true }!!)
        }
    }
}