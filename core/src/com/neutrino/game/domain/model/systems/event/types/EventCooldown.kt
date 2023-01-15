package com.neutrino.game.domain.model.systems.event.types

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.Event
import com.neutrino.game.domain.model.systems.event.Timed
import com.neutrino.game.domain.model.systems.skills.Skill

class EventCooldown(): Event(), Timed {
    constructor(character: Character, cooldownType: CooldownType, cooldownLength: Double) : this() {
        this.character = character
        this.cooldownType = cooldownType
        this.cooldownLength = cooldownLength
    }

    constructor(cooldownType: CooldownType, cooldownLength: Double) : this() {
        this.cooldownType = cooldownType
        this.cooldownLength = cooldownLength
    }

    override val data: MutableMap<String, Data<*>> = mutableMapOf(
        Pair("character", Data<Character>()),
        Pair("cooldownType", Data<CooldownType>()),
        Pair("cooldownLength", Data<Double>())
    )

    var character: Character
        get() { return get("character", Character::class)!! }
        set(value) { set("character", value) }
    var cooldownType: CooldownType
        get() { return get("cooldownType", CooldownType::class)!! }
        set(value) { set("cooldownType", value) }
    var cooldownLength: Double
        get() { return get("cooldownLength", Double::class)!! }
        set(value) {
            set("cooldownLength", value)
            turnDelay = value
        }

    override var turnDelay: Double = 0.0
    override val timeout: Double = 0.0
    override var executions: Int = 1
}

sealed class CooldownType {
    object FOOD: CooldownType()
    data class SKILL(val skill: Skill): CooldownType()
    /** Cooldown for a specific item */
    data class ITEM(val itemName: String): CooldownType()
    object NONE: CooldownType()
}