package com.neutrino.game.domain.model.items

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.turn.Event

sealed interface ItemType {
    interface WEAPON: ItemType
    interface EQUIPMENT: ItemType
    interface SCROLL: ItemType
    interface EDIBLE: ItemType {
        val isFood: Boolean
        val powerOg: Float
        val speedOg: Double
        val repeatsOg: Int
        val power: Float
        val speed: Double
        val repeats: Int
        fun use(character: Character, turn: Double): Event {
            return Event.HEAL(character, isFood, power, turn, speed, repeats)
        }
        fun getEffectLength(): Double {
            return speed * (repeats - 1)
        }
    }
    interface KEY: ItemType
    interface MISC: ItemType
}