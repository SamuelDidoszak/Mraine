package com.neutrino.game.domain.model.turn
import com.neutrino.game.domain.model.characters.Character

data class Event(
    val character: Character,
    val turn: Double,
    val eventType: EventType,
    val value: Int
) {
    enum class EventType {
        COOLDOWN,
        EFFECT
    }
}