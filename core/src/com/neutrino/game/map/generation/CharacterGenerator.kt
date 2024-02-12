package com.neutrino.game.map.generation

import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.entities.Characters
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.generation.util.GenerationParams
import com.neutrino.game.map.level.CharacterArray
import com.neutrino.game.util.hasIdentity
import squidpony.squidmath.Coord
import kotlin.math.roundToInt

class CharacterGenerator(val params: GenerationParams, val levelDrawer: LevelDrawer) {

    val characterArray = CharacterArray()
    val characterMap: List<MutableList<Entity?>> = List(params.map.size) {
        MutableList(params.map[0].size) {null}
    }

    fun generate(): CharacterArray {
        // TODO Map generation
        val difficultyModifier = kotlin.math.abs(params.level.chunkCoords.z)
        params.interpretedTags.tagParams.difficulty += difficultyModifier / 4

        addPlayerAtStairs()
        spawnEnemies()
        return characterArray
    }

    fun addPlayerAtStairs() {
        var stairsDown: Coord? = null
        var stairsUp: Coord? = null
        for (y in 0 until params.map.size) {
            for (x in 0 until params.map[0].size) {
                for (z in 0 until params.map[y][x].size) {
                    if (params.map[y][x][z] hasIdentity Identity.StairsDown::class)
                        stairsDown = Coord.get(x, y)
                    if (params.map[y][x][z] hasIdentity Identity.StairsUp::class)
                        stairsUp = Coord.get(x, y)
                }
            }
            if (stairsUp != null && stairsDown != null)
                break
        }

        if (Player hasNot Position::class) {
            Player.addAttribute(DrawPosition())
            Player.addAttribute(Position(0, 0, levelDrawer))
        }
        Player.addAttribute(com.neutrino.game.entities.map.attributes.Turn(0.0))

        if (params.level.chunkCoords.z > 0) {
            Player.get(Position::class)!!.x = stairsUp!!.x
            Player.get(Position::class)!!.y = stairsUp.y
        }
        else {
            val coord = stairsDown ?: (getRandomPosition()?: Coord.get(30, 30))
            Player.get(Position::class)!!.x = coord.getX()
            Player.get(Position::class)!!.y = coord.getY()
        }

        characterArray.add(Player)
        characterMap[Player.get(Position::class)!!.y][Player.get(Position::class)!!.x] = Player
    }


    private fun spawnEnemies() {
        for (i in 0 until (
                20 * params.interpretedTags.tagParams.enemyMultiplier * (2f - params.interpretedTags.tagParams.enemyQuality))
            .roundToInt()) {
//            try {
                val character = getCharacter()
                characterArray.add(character)
                characterMap[character.get(Position::class)!!.y][character.get(Position::class)!!.x] = character
//            } catch (e: Exception) {
//                println("Error: ${e.message}")
//                break
//            }
        }
    }

    @Throws(Exception::class)
    private fun getCharacter(): Entity {
        val currentTurn = Turn.turn
        // TODO Amount and the type of enemies should be dependant on level difficulty and enemy difficulty
        val coord = getRandomPosition()!!
        val character: Entity = Characters.new("Mouse")
        character.addAttribute(DrawPosition())
        character.addAttribute(Position(coord.getX(), coord.getY(), levelDrawer))
        character.addAttribute(com.neutrino.game.entities.map.attributes.Turn(currentTurn))
        // TODO ECS Items
//        character.randomize(params.rng)
        return character
    }

    private fun getRandomPosition(): Coord? {
        // prevents searching for a position indefinitely. Max searches 50 times
        var tries: Int = 0

        try {
            var xPos: Int
            var yPos: Int
            do {
                xPos = params.rng.nextInt(0, params.map[0].size)
                yPos = params.rng.nextInt(0, params.map.size)
                if (tries++ == 50)
                    throw Exception("Couldn't find more positions")
                // possibly change it to movementMap for efficiency. It has inverted xPos and yPos
            } while (!params.level.allowsCharacter(xPos, yPos) || characterMap[yPos][xPos] != null)

            return Coord.get(xPos, yPos)
        } catch (e: Exception) {e.toString()}
        return null
    }
}