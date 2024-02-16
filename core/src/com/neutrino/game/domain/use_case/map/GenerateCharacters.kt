package com.neutrino.game.domain.use_case.map

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.Rat
import com.neutrino.game.map.level.Chunk
import com.neutrino.game.domain.model.map.MapTags
import com.neutrino.game.domain.model.map.TagInterpretation
import com.neutrino.game.domain.model.turn.CharacterArray
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.util.hasIdentity
import squidpony.squidmath.Coord
import kotlin.math.roundToInt

class GenerateCharacters(
    private val chunk: Chunk
) {
    val characterArray = CharacterArray()

    val characterMap: List<MutableList<Character?>> = List(chunk.sizeY) {
        MutableList<Character?>(chunk.sizeX) {null}
    }

    private val interpretedTags = TagInterpretation(listOf(MapTags.CHARACTERS))

    fun generate(): CharacterArray {
        val difficultyModifier = kotlin.math.abs(chunk.chunkCoords.z)
        interpretedTags.generationParams.difficulty += difficultyModifier / 4

        addPlayerAtStairs()
        spawnEnemies()
        return characterArray
    }

    fun addPlayerAtStairs() {
        var stairsDown: Coord? = null
        var stairsUp: Coord? = null
        for (y in 0 until chunk.sizeY) {
            for (x in 0 until chunk.sizeX) {
                for (z in 0 until chunk.map[y][x].size) {
                    if (chunk.map[y][x][z] hasIdentity Identity.StairsDown::class)
                        stairsDown = Coord.get(x, y)
                    if (chunk.map[y][x][z] hasIdentity Identity.StairsUp::class)
                        stairsUp = Coord.get(x, y)
                }
            }
            if (stairsUp != null && stairsDown != null)
                break
        }

        if (chunk.chunkCoords.z > 0) {
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
        character.randomize(chunk.randomGenerator)
        return character
    }

    private fun getRandomPosition(): Coord? {
        // prevents searching for a position indefinitely. Max searches 50 times
        var tries: Int = 0

        try {
            var xPos: Int
            var yPos: Int
            do {
                xPos = chunk.randomGenerator.nextInt(0, chunk.sizeX)
                yPos = chunk.randomGenerator.nextInt(0, chunk.sizeY)
                if (tries++ == 50)
                    throw Exception("Couldn't find more positions")
                // possibly change it to movementMap for efficiency. It has inverted xPos and yPos
            } while (!chunk.allowsCharacter(xPos, yPos) || characterMap[yPos][xPos] != null)

            return Coord.get(xPos, yPos)
        } catch (e: Exception) {e.toString()}
        return null
    }
}