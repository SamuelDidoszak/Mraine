package com.neutrino.game.domain.model.items

import com.neutrino.game.domain.model.event.CausesCooldown
import com.neutrino.game.domain.model.event.CausesEvents

sealed interface ItemType {
    interface USABLE: ItemType, CausesEvents

    interface EDIBLE: ItemType, CausesEvents, CausesCooldown {
        val isFood: Boolean
        val powerOg: Float
        val timeoutOg: Double
        val executionsOg: Int
        val power: Float
        val timeout: Double
        val executions: Int
        fun getEffectLength(): Double {
            return executions * timeout
        }
    }
    interface KEY: ItemType
    interface MISC: ItemType

    sealed interface EQUIPMENT: ItemType {
        interface HEAD: EQUIPMENT
        interface TORSO: EQUIPMENT
        interface LEGS: EQUIPMENT
        interface HANDS: EQUIPMENT
        interface FEET: EQUIPMENT
        interface AMULET: EQUIPMENT
        interface LRING: EQUIPMENT
        interface RRING: EQUIPMENT
        interface BAG: EQUIPMENT
        interface LHAND: EQUIPMENT, INHAND
        interface RHAND: EQUIPMENT, INHAND
        interface TWOHAND: EQUIPMENT, INHAND
    }
}
interface INHAND {
    val handedItemType: HandedItemType
}

enum class HandedItemType {
    SHIELD,

    // Weapon types
    // melee
    DAGGER,
    SWORD,
    AXE,
    LANCE,

    // ranged
    BOW,
    CROSSBOW,
    ARROW,

    // magic
    WAND,
    STAFF,
    PARCHMENT
}