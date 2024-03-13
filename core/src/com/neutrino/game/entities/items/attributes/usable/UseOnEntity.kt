package com.neutrino.game.entities.items.attributes.usable

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events

class UseOnEntity(
    val charactersOnly: Boolean,
    val use: (entity: Entity, targetEntity: Entity) -> Unit = { entity, targetEntity ->
        entity.get(UseEvents::class)?.events?.forEach {
            if (it.event is CharacterEvents)
                it.event.entity = targetEntity
            Events.addEvent(targetEntity, it)
        }
    }
): Attribute() {

    fun use(targetEntity: Entity) = use.invoke(entity, targetEntity)
}