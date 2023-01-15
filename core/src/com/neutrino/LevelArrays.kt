package com.neutrino

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.turn.CharacterArray
import com.neutrino.game.domain.model.turn.Turn
import squidpony.squidmath.Coord

object LevelArrays {
    fun getCharacterArray(): CharacterArray {
        return Turn.characterArray
    }

    fun getCharacterMap(): List<MutableList<Character?>> {
        return Turn.characterMap
    }

    fun getCharacterAt(coord: Coord): Character? {
        return Turn.characterMap[coord.y][coord.x]
    }

    fun getCharacterAt(x: Int, y: Int): Character? {
        return Turn.characterMap[y][x]
    }

    fun getMap(): List<List<MutableList<Entity>>> {
        return Turn.currentLevel.map.map
    }

    fun getEntitiesAt(coord: Coord): List<Entity> {
        return Turn.currentLevel.map.map[coord.y][coord.x]
    }

    fun getEntitiesAt(x: Int, y: Int): List<Entity> {
        return Turn.currentLevel.map.map[y][x]
    }
}