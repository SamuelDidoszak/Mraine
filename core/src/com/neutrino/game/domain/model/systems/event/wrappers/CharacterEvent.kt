package com.neutrino.game.domain.model.systems.event.wrappers

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.systems.event.Event

data class CharacterEvent(
    val character: Character,
    /** Turn of execution */
    var turn: Double,
    /** Length of delay before the next execution */
    val timeout: Double,
    /** How many times the event will occur */
    var executions: Int,
    override val event: Event
): EventWrapper(event) {
    var curRepeat: Int = 0

    constructor(character: Character, timedEvent: TimedEvent, turn: Double) :
            this(character, turn + timedEvent.turnDelay, timedEvent.timeout, timedEvent.executions, timedEvent.event)
}