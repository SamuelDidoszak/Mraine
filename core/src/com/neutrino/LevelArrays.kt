package com.neutrino

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.model.turn.CharacterArray
import com.neutrino.game.domain.model.turn.Turn
import squidpony.squidmath.Coord

object LevelArrays {
    private fun levelDispatcher(): Level {
        return Turn.currentLevel
    }

    private fun levelDispatcher(coord: Coord): Level {
        if (coord.x !in 0 until 100 || coord.y !in 0 until 100) {
            // TODO return adjacent level
        }
        return levelDispatcher()
    }

    /**
     * If coord is outside of map, returns an appropriate coord from adjacent level
     */
    private fun parseCoord(coord: Coord): Coord {
        val x =
            if (coord.x < 0)
                0
            else if (coord.x >= 100)
                99
            else
                coord.x
        val y =
            if (coord.y < 0)
                0
            else if (coord.y >= 100)
                99
            else
                coord.y
        return Coord.get(x, y)
    }

    fun getLevel(): Level {
        return levelDispatcher()
    }

    fun getDiscoveredMap(): List<MutableList<Boolean>> {
        return levelDispatcher().discoveredMap
    }

    fun getDiscoveredAt(coord: Coord): Boolean {
        return levelDispatcher().discoveredMap[coord.y][coord.x]
    }

    fun getCharacterArray(): CharacterArray {
        return Turn.characterArray
    }

    fun getCharacterMap(): List<MutableList<Character?>> {
        return Turn.characterMap
    }

    fun getCharacterAt(coord: Coord): Character? {
        val coord = parseCoord(coord)
        return Turn.characterMap[coord.y][coord.x]
    }

    fun getCharacterAt(x: Int, y: Int): Character? {
        val coord = parseCoord(Coord.get(x, y))
        return Turn.characterMap[coord.y][coord.x]
    }

    fun getMap(): List<List<MutableList<Entity>>> {
        return Turn.currentLevel.map.map
    }

    fun getEntitiesAt(coord: Coord): MutableList<Entity> {
        val coord = parseCoord(coord)
        return Turn.currentLevel.map.map[coord.y][coord.x]
    }

    fun getEntitiesAt(x: Int, y: Int): MutableList<Entity> {
        val coord = parseCoord(Coord.get(x, y))
        return Turn.currentLevel.map.map[coord.y][coord.x]
    }

    fun getImpassableList(): ArrayList<Coord> {
        return Turn.mapImpassableList
    }

    fun isImpassable(coord: Coord): Boolean {
        for (entity in getEntitiesAt(coord)) {
            if (!entity.allowCharacterOnTop) {
                return true
            }
        }
        return false
    }
}