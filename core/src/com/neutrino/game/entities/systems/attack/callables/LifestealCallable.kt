package com.neutrino.game.entities.systems.attack.callables

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.attributes.CharacterTags
import com.neutrino.game.entities.characters.attributes.util.CharacterTag
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.events.TimedEvent

class LifestealCallable: AttackedAfterCallable() {

    /**
     * @param data [[0]]: Entity. Attacked entity
     * @param data [[1]]: Float. Damage dealt
     */
    override fun call(entity: Entity, vararg data: Any?) {
        if (data[0] !is Character || data[1] !is Float)
            return

        Events.addEvent(entity,
            TimedEvent(CharacterEvents.Heal(
                (data[1] as Float) * (entity.get(CharacterTags::class)?.getTag(CharacterTag.Lifesteal::class)?.power ?: 0f)
            )
        ))
    }
}