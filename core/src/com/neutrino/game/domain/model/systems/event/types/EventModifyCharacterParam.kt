package com.neutrino.game.domain.model.systems.event.types

import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.CharacterParamsEnum
import com.neutrino.game.domain.model.characters.utility.HasInventory
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.Event

class EventModifyCharacterParam(): Event() {
    constructor(param: CharacterParamsEnum) : this() {
        this.param = param
    }

    constructor(param: CharacterParamsEnum, value: Any) : this() {
        this.param = param
        this.value = value
    }

    override val data: MutableMap<String, Data<*>> = mutableMapOf(
        Pair("character", Data<Character>()),
        Pair("param", Data<CharacterParamsEnum>()),
        Pair("value", Data<Any>())
    )
    var character: Character
        get() { return get("character", Character::class)!! }
        set(value) { set("character", value) }

    var param: CharacterParamsEnum
        get() { return get("param", CharacterParamsEnum::class)!! }
        set(value) { set("param", value) }
    var value: Any
        get() { return get("value", Any::class)!! }
        set(value) { set("value", value) }

    override fun start() {
        checkData()

        when(param) {
            CharacterParamsEnum.INVENTORYSIZE -> {
                if (character is HasInventory)
                    (character as HasInventory).inventory.size += value as Int
                if (character is Player)
                    GlobalData.notifyObservers(GlobalDataType.PLAYERINVENTORYSIZE)
            }
        }
    }

    override fun stop() {
        checkData()

        when(param) {
            CharacterParamsEnum.INVENTORYSIZE -> {
                if (character is HasInventory)
                    (character as HasInventory).inventory.size -= value as Int
                if (character is Player)
                    GlobalData.notifyObservers(GlobalDataType.PLAYERINVENTORYSIZE)
            }
        }
    }
}