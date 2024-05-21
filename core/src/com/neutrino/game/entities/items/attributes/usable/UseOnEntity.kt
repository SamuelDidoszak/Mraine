package com.neutrino.game.entities.items.attributes.usable

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.requirements.PrintableInfo

class UseOnEntity(
    val charactersOnly: Boolean,
    val use: (entity: Entity, targetEntity: Entity) -> Unit = { entity, targetEntity ->
        entity.get(UseEvents::class)?.events?.forEach {
            if (it.event is CharacterEvents)
                it.event.entity = targetEntity
            Events.addEvent(targetEntity, it)
        }
    }
): Attribute(), PrintableInfo<UseOnEntity> {

    fun use(targetEntity: Entity) = use.invoke(entity, targetEntity)

    override fun getPrintableInfo(other: UseOnEntity?): List<Pair<String, Any?>> {
        val printableInfo = ArrayList<Pair<String, Any?>>()
        entity.get(UseEvents::class)?.events?.forEach { timedEvent ->
            printableInfo.add(timedEvent.name to
                    timedEvent.printable(other?.entity?.get(UseEvents::class)?.events?.find { it.name == timedEvent.name }))
        }
        return printableInfo
    }
}