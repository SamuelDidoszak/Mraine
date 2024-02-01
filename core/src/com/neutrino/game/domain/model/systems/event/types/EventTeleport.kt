package com.neutrino.game.domain.model.systems.event.types

import com.neutrino.LevelArrays
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.Event
import com.neutrino.game.domain.model.turn.Turn
import squidpony.squidmath.Coord

class EventTeleport(): Event() {
    constructor(character: Character, coord: Coord) : this() {
        this.character = character
        this.coord = coord
    }

    constructor(coord: Coord) : this() {
        this.coord = coord
    }

    override val data: MutableMap<String, Data<*>> = mutableMapOf(
        Pair("character", Data<Character>()),
        Pair("coord", Data<Coord>())
    )

    var character: Character
        get() { return get("character", Character::class)!! }
        set(value) { set("character", value) }

    var coord: Coord
        get() { return get("coord", Coord::class)!! }
        set(value) { set("coord", value) }

    override fun start() {
        if (!checkData())
            return

        LevelArrays.getCharacterMap()[character.yPos][character.xPos] = null
        LevelArrays.getCharacterMap()[coord.y][coord.x] = character
        Turn.mapFov.updateFov(coord.x, coord.y, character.ai.fov, character.viewDistance)
        character.move(coord.x, coord.y, 0f)
    }

    override fun toString(): String {
        return "Teleport"
    }
}