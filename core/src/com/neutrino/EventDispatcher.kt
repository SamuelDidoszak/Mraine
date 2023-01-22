package com.neutrino

import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.turn.Turn

object EventDispatcher {
    fun dispatchEvent(eventWrapper: EventWrapper) {
        when (eventWrapper) {
            is CharacterEvent -> {
                Turn.eventArray.startEvent(eventWrapper)
            }
        }
    }

    fun removeEvent(eventWrapper: EventWrapper) {
        when (eventWrapper) {
            is CharacterEvent -> {
                Turn.eventArray.stopEvent(eventWrapper)
            }
        }
    }
}