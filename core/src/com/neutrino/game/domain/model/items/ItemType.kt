package com.neutrino.game.domain.model.items

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.turn.Event

sealed interface ItemType {
    interface WEAPON: ItemType
    interface EQUIPMENT: ItemType
    interface CAUSESEVENT: ItemType {
        fun use(character: Character, turn: Double): Event
    }
    sealed interface SCROLL: ItemType {
//        fun use(character: Character, turn: Double): Event
//        fun use(character: Character, turn: Double, length: Double): Event
        interface STAT: SCROLL {
            val statName: String
            val power: Any
            val speed: Double
            val repeats: Int
            fun use(character: Character, turn: Double): Event {
                return Event.MODIFYSTAT(
                    character, statName, power, turn, speed, repeats
                )
            }
            fun getEffectLength(): Double {
                // repeats -1??? Idk, test it out
                return speed * repeats
            }
        }
        interface ATTACK: SCROLL {

        }
        interface EFFECT: SCROLL {

        }
    }
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
            // repeats -1??? Idk, test it out
            return speed * (repeats - 1)
        }
    }
    interface KEY: ItemType
    interface MISC: ItemType
}