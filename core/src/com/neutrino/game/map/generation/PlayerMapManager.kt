package com.neutrino.game.map.generation

import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.util.Constants
import com.neutrino.game.util.SeedUtil
import com.neutrino.game.util.hasIdentity
import squidpony.squidmath.Coord
import kotlin.random.Random

class PlayerMapManager {

    fun addPlayer(chunk: Chunk) {
        if (chunk.chunkCoords.z != 0) {
            addPlayerAtStairs(chunk)
            return
        }

        if (Player hasNot Position::class) {
            Player.addAttribute(DrawPosition())
            Player.addAttribute(Position(0, 0, chunk.chunkCoords))
        }
        Player.addAttribute(com.neutrino.game.entities.map.attributes.Turn(0.0))

        val coord = getRandomPosition(chunk)?: Coord.get(30, 30)
        Player.get(Position::class)!!.x = coord.getX()
        Player.get(Position::class)!!.y = coord.getY()

        chunk.characterArray.add(Player)
        chunk.characterMap[Player.get(Position::class)!!.y][Player.get(Position::class)!!.x] = Player

    }

    fun addPlayerAtStairs(chunk: Chunk) {
        var stairsDown: Coord? = null
        var stairsUp: Coord? = null
        for (y in 0 until chunk.map.size) {
            for (x in 0 until chunk.map[0].size) {
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

        if (Player hasNot Position::class) {
            Player.addAttribute(DrawPosition())
            Player.addAttribute(Position(0, 0, chunk.chunkCoords))
        }
        Player.addAttribute(com.neutrino.game.entities.map.attributes.Turn(0.0))

        if (chunk.chunkCoords.z > 0) {
            Player.get(Position::class)!!.x = stairsUp!!.x
            Player.get(Position::class)!!.y = stairsUp.y
        }
        else {
            val coord = stairsDown ?: (getRandomPosition(chunk)?: Coord.get(30, 30))
            Player.get(Position::class)!!.x = coord.getX()
            Player.get(Position::class)!!.y = coord.getY()
        }

        chunk.characterArray.add(Player)
        chunk.characterMap[Player.get(Position::class)!!.y][Player.get(Position::class)!!.x] = Player
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
        } catch (e: Exception) {e.toString()}
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