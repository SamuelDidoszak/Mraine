package com.neutrino.game.entities.map_entities.util

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map_entities.attributes.Chest
import com.neutrino.game.entities.map_entities.attributes.Door
import com.neutrino.game.entities.map_entities.attributes.PickUp
import com.neutrino.game.util.add

interface Interactable {

    fun interact() {}
    var requiredDistance: Int
    var turnCost: Double
    var isPrimary: Boolean

    companion object {
        fun getAllInteractions(entity: Entity): List<Interactable>? {
            val interactions = ArrayList<Interactable>()
            interactions.add(entity.get(PickUp::class) as? Interactable)
            interactions.add(entity.get(Door::class) as? Interactable)
            interactions.add(entity.get(Chest::class) as? Interactable)
            return interactions.ifEmpty { null }
        }

        fun getPrimaryInteraction(entity: Entity): Interactable? {
            if (entity.get(PickUp::class)?.isPrimary == true)
                return entity.get(PickUp::class)
            if (entity.get(Door::class)?.isPrimary == true)
                return entity.get(Door::class)
            if (entity.get(Chest::class)?.isPrimary == true)
                return entity.get(Chest::class)
            return null
        }
    }
}