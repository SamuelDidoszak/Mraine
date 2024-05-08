package com.neutrino.game.entities.characters.callables.attack

import com.neutrino.game.domain.model.systems.CharacterTag
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.CharacterTags
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.events.TimedEvent

class LifestealCallable: AttackedAfterCallable() {

    override fun call(entity: Entity, vararg data: Any?): Boolean {
        if (data[1] !is Entity || data[2] !is Float)
            return true

        Events.addEvent(entity,
            TimedEvent(CharacterEvents.Heal(
                (data[1] as Float) * (entity.get(CharacterTags::class)?.getTag(CharacterTag.Lifesteal::class)?.power ?: 0f))
        ))
        return true
    }
}