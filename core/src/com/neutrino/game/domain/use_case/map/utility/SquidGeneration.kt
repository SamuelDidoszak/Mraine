package com.neutrino.game.domain.use_case.map.utility

import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.util.Constants
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.Wall
import com.neutrino.game.map.level.Chunk
import com.neutrino.game.util.hasSuper
import squidpony.squidgrid.mapping.DungeonGenerator
import squidpony.squidgrid.mapping.styled.TilesetType
import squidpony.squidmath.Coord
import squidpony.squidmath.GWTRNG
import squidpony.squidmath.IRNG
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


class SquidGeneration (
    val chunk: Chunk
) {
    private val irng: IRNG = GWTRNG(Constants.Seed + chunk.id)
    private val dungeonGenerator = DungeonGenerator(chunk.sizeX, chunk.sizeY, irng)
    private lateinit var dungeonLayout: Array<out CharArray>

    fun generateDungeon(tilesetType: TilesetType) {
        dungeonLayout = dungeonGenerator.addDoors(100, true)
            .generate(tilesetType)
        GlobalData.registerData(GlobalDataType.CHANGELEVEL, tilesetType.name)
    }

    fun setWalls(map: List<List<MutableList<Entity>>>, wall: KClass<out Wall>) {
        for (y in 0 until chunk.sizeY) {
            for (x in 0 until chunk.sizeX) {
                if (dungeonLayout[y][x] == '#')
                    map[y][x].add(wall.createInstance())
            }
        }
    }

    fun setEntrances(map: List<List<MutableList<Entity>>>, down: KClass<out Entity>, up: KClass<out Entity>) {
        val stairsDown = correctStairs(map, dungeonGenerator.stairsDown)
        val stairsUp = correctStairs(map, dungeonGenerator.stairsUp)

        map[stairsDown.y][stairsDown.x].add(down.createInstance())
        map[stairsUp.y][stairsUp.x].add(up.createInstance())
    }

    private fun correctStairs(map: List<List<MutableList<Entity>>>, stairs: Coord): Coord {
        var stairsCorrected: Coord? = if (map[stairs.y][stairs.x] hasSuper Wall::class) null else stairs

        for (y in -1..1) {
            if (stairsCorrected != null)
                break
            if (stairs.y + y !in map.indices)
                continue
            for (x in -1..1) {
                if (stairs.x + x !in map[stairs.y].indices)
                    continue

                if (!map[stairs.y + y][stairs.x + x].hasSuper(Wall::class)) {
                    stairsCorrected = Coord.get(stairs.x + x, stairs.y + y)
                    break
                }
            }
        }

        if (stairsCorrected != null)
            return stairsCorrected

        // randomize position if no coord fount
        do {
            stairsCorrected = Coord.get(
                chunk.randomGenerator.nextInt(0, chunk.sizeX),
                chunk.randomGenerator.nextInt(0, chunk.sizeY)
            )
        } while (map[stairsCorrected!!.y][stairsCorrected.x] hasSuper Wall::class)

        return stairsCorrected
    }
}