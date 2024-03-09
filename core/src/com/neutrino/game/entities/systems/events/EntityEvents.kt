package com.neutrino.game.entities.systems.events

import com.neutrino.game.entities.Entities
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.util.EntityName

abstract class EntityEvents: Event {

    class Spawn(val entity: EntityName, val position: Position,
                val apply: (entity: Entity) -> Unit): EntityEvents() {
        override fun apply() {
            val addedEntity = Entities.new(entity)
            apply.invoke(addedEntity)
            addedEntity.addAttribute(position)
            position.chunk.map[position.y][position.x].add(addedEntity)
        }
    }
}