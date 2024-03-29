package com.neutrino.game.domain.model.systems.event.types

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.Event

/**
 * This class is only for displaying information
 */
class EventBerserk(): Event() {
    constructor(character: Character, power: Float) : this() {
        this.character = character
        this.power = power
    }

    constructor(power: Float) : this() {
        this.power = power
    }

    override val data: MutableMap<String, Data<*>> = mutableMapOf(
        Pair("character", Data<Character>()),
        Pair("power", Data<Float>())
    )

    var character: Character
        get() { return get("character", Character::class)!! }
        set(value) { set("character", value) }

    var power: Float
        get() { return get("power", Float::class)!! }
        set(value) { set("power", value) }

    override fun start() {
        if (!checkData())
            return

        // This class is only for displaying information
    }

    override fun toString(): String {
        return "Berserk"
    }
}