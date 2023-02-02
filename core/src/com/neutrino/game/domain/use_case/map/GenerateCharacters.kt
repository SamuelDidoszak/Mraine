package com.neutrino.game.domain.use_case.map

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.Rat
import com.neutrino.game.domain.model.entities.DungeonStairsDown
import com.neutrino.game.domain.model.entities.DungeonStairsUp
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.model.map.TagInterpretation
import com.neutrino.game.domain.model.turn.CharacterArray
import com.neutrino.game.domain.model.turn.Turn
import squidpony.squidmath.Coord
import kotlin.math.roundToInt

class GenerateCharacters(
    private val level: Level
) {
    val characterArray = CharacterArray()

    val characterMap: List<MutableList<Character?>> = List(level.sizeY) {
        MutableList<Character?>(level.sizeX) {null}
    }

    private val interpretedTags = TagInterpretation(level.tagList)

    fun generate(): CharacterArray {
        val difficultyModifier = kotlin.math.abs(level.levelChunkCoords.z)
        interpretedTags.generationParams.difficulty += difficultyModifier / 4

        // TODO temporary
        var stairsDown: Coord? = null
        var stairsUp: Coord? = null
        for (y in 0 until level.map.yMax) {
            for (x in 0 until level.map.xMax) {
                for (z in 0 until level.map.map[y][x].size) {
                    if (level.map.map[y][x][z] is DungeonStairsDown)
                        stairsDown = Coord.get(x, y)
                    if (level.map.map[y][x][z] is DungeonStairsUp)
                        stairsUp = Coord.get(x, y)
                }
            }
            if (stairsUp != null && stairsDown != null)
                break
        }

        if (level.levelChunkCoords.z > 0) {
            Player.xPos = stairsUp!!.x
            Player.yPos = stairsUp.y
        }
        else {
            val coord = stairsDown ?: (getRandomPosition()?: Coord.get(30, 30))
            Player.xPos = coord.getX()
            Player.yPos = coord.getY()
        }

        characterArray.add(Player)
        characterMap[Player.yPos][Player.xPos] = Player
        spawnEnemies()
        return characterArray
    }


    private fun spawnEnemies() {
        for (i in 0 until (20 * interpretedTags.generationParams.enemyMultiplier * (2f - interpretedTags.generationParams.enemyQuality)).roundToInt()) {
            try {
                val character = getCharacter()
                characterArray.add(character)
                characterMap[character.yPos][character.xPos] = character
            } catch (e: Exception) {
                println("Error: ${e.message}")
                break
            }
        }
    }

    @Throws(Exception::class)
    private fun getCharacter(): Character {
        val currentTurn = Turn.turn
        // TODO Amount and the type of enemies should be dependant on level difficulty and enemy difficulty
        val coord = getRandomPosition()!!
        val character: Character = Rat(coord.getX(), coord.getY(), currentTurn)
        character.randomize(level.randomGenerator)
        return character
    }

    private fun getRandomPosition(): Coord? {
        // prevents searching for a position indefinitely. Max searches 50 times
        var tries: Int = 0

        try {
            var xPos: Int
            var yPos: Int
            do {
                xPos = level.randomGenerator.nextInt(0, level.sizeX)
                yPos = level.randomGenerator.nextInt(0, level.sizeY)
                if (tries++ == 50)
                    throw Exception("Couldn't find more positions")
                // possibly change it to movementMap for efficiency. It has inverted xPos and yPos
            } while (!level.allowsCharacter(xPos, yPos) || characterMap[yPos][xPos] != null)

            return Coord.get(xPos, yPos)
        } catch (e: Exception) {e.toString()}
        return null
    }
}