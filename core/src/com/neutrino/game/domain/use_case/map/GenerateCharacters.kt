package com.neutrino.game.domain.use_case.map

import com.neutrino.game.Constants.RandomGenerator
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.Rat
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.model.turn.CharacterArray
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.domain.use_case.characters.GetImpassable
import squidpony.squidmath.Coord

class GenerateCharacters(
    private val level: Level
) {
    val characterArray = CharacterArray()

    operator fun invoke(): CharacterArray {
        if (level.zPosition != 0)
            // TODO Spawn player on the stairs
            println("level is a dungeon")
        else {
            val coord = getRandomPosition()!!
            Player.xPos = coord.getX()
            Player.yPos = coord.getY()
            characterArray.add(Player)
        }

        spawnEnemies()
        return characterArray
    }


    private fun spawnEnemies() {
        // TODO Amount and the type of enemies should be dependant on level difficulty and enemy difficulty
        for (i in 0 until 1) {
            try {
                characterArray.add(getCharacter())
            } catch (e: Exception) {
                println(e.message)
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
        return character
    }

    private fun getRandomPosition(): Coord? {
        // prevents searching for a position indefinitely. Max searches 50 times
        var tries: Int = 0

        try {
            var xPos: Int
            var yPos: Int
            do {
                xPos = RandomGenerator.nextInt(0, level.sizeX)
                yPos = RandomGenerator.nextInt(0, level.sizeY)
                if (tries++ == 50)
                    throw Exception("Couldn't find more positions")
                // possibly change it to movementMap for efficiency. It has inverted xPos and yPos
            } while (!level.doesAllowCharacter(xPos, yPos) && !GetImpassable(characterArray)().contains(Coord.get(xPos, yPos)))

            return Coord.get(xPos, yPos)
        } catch (e: Exception) {e.toString()}
        return null
    }
}