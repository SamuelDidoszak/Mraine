package com.neutrino.game.map.generation

import com.neutrino.game.entities.Characters
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.Loot
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.gameplay.turn.Turn
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.generation.worldgen.LootTable
import com.neutrino.game.util.Constants
import com.neutrino.game.util.SeedUtil
import squidpony.squidmath.Coord
import kotlin.random.Random

class CharacterGenerator {

    fun generate(chunk: Chunk) {
        spawnEnemies(chunk)
//        generateLoot(chunk)
    }


    private fun spawnEnemies(chunk: Chunk) {
        for (i in 0 until 2) {
            val character = getCharacter(chunk)
            chunk.characterArray.add(character)
            chunk.characterMap[character.get(Position::class)!!.y][character.get(Position::class)!!.x] = character
        }
    }

    private fun generateLoot(chunk: Chunk) {
        val rng = Random(SeedUtil.branch(Constants.Seed, "Loot ${chunk.chunkCoords}"))
        chunk.characterArray.forEach {
            it.get(Loot::class)?.generateLoot(rng)
            it.get(LootTable::class)?.rollLootEntry(rng)
        }
    }

    @Throws(Exception::class)
    private fun getCharacter(chunk: Chunk): Entity {
        val currentTurn = Turn.turn
        // TODO Amount and the type of enemies should be dependant on level difficulty and enemy difficulty
        val coord = getRandomPosition(chunk)!!
        val character: Entity = Characters.new("Mouse")
        character.addAttribute(DrawPosition())
        character.addAttribute(Position(coord.getX(), coord.getY(), chunk.chunkCoords))
        character.addAttribute(com.neutrino.game.entities.map.attributes.Turn(currentTurn))
        // TODO ECS Characters
//        character.randomize(params.rng)
        return character
    }

    private fun getRandomPosition(chunk: Chunk): Coord? {
        val rng = Random(SeedUtil.branch(Constants.Seed, "Random position in ${chunk.chunkCoords}"))
        // prevents searching for a position indefinitely. Max searches 50 times
        var tries: Int = 0

        try {
            var xPos: Int
            var yPos: Int
            do {
                xPos = rng.nextInt(0, chunk.map[0].size)
                yPos = rng.nextInt(0, chunk.map.size)
                if (tries++ == 50)
                    throw Exception("Couldn't find more positions")
                // possibly change it to movementMap for efficiency. It has inverted xPos and yPos
            } while (!allowsCharacter(xPos, yPos, chunk) || chunk.characterMap[yPos][xPos] != null)

            return Coord.get(xPos, yPos)
        } catch (e: Exception) { println(e.toString()) }
        return null
    }

    private fun allowsCharacter(xPos: Int, yPos: Int, chunk: Chunk): Boolean {
        var allow = true
        for (entity in chunk.map[yPos][xPos]) {
            if (!entity.get(MapParams::class)!!.allowCharacterOnTop) {
                allow = false
                break
            }
        }
        return allow
    }
}