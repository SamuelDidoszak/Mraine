package com.neutrino.game.domain.model.systems.event

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent

object EventToPrintableMapper {
    fun getPrintable(wrapper: EventWrapper): Pair<String, Any> {
        when (wrapper) {
            is OnOffEvent -> {
                return getPrintable(wrapper.event)
            }
            is TimedEvent -> {
                val printedEvent = getPrintable(wrapper.event)
                return Pair(
                    printedEvent.first +
                    "/${wrapper.cooldown} turn. " +
                    "Lasts ${wrapper.executions * wrapper.cooldown} turns",
                    printedEvent.second
                )
            }
            is CharacterEvent -> {
                return getPrintable(wrapper.event)
            }
            else -> {
                return getPrintable(wrapper.event)
            }
        }
    }

    fun getPrintable(event: Event): Pair<String, Any> {
        when (event) {
            is EventModifyStat -> {
                return Pair(event.stat.toString(), event.value.toString() + if (event.percent) "%" else "")
            }
            else -> {
                return Pair(event.toString(), "")
            }
        }
    }

    fun getIcon(wrapper: EventWrapper): Image {
        return Image()
    }
}