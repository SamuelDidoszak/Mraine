package com.neutrino.game.domain.model.items

import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.attack.Attack
import com.neutrino.game.domain.model.systems.event.CausesCooldown
import com.neutrino.game.domain.model.systems.event.CausesEvents
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper

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
    var attack: Attack
    fun getDamageTypesFromModifiers(modifierList: ArrayList<EventWrapper>): Map<StatsEnum, Float> {
        return modifierList.mapNotNull { if (it.event is EventModifyStat) (it.event as EventModifyStat).stat to 0f else null }.toMap()
    }
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