package com.neutrino.game.domain.use_case.map

import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.map.Level
import squidpony.squidgrid.mapping.DungeonGenerator
import squidpony.squidgrid.mapping.styled.TilesetType
import squidpony.squidmath.GWTRNG
import squidpony.squidmath.IRNG
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


class SquidGeneration (
    val level: Level
) {
    private val irng: IRNG = GWTRNG(Constants.Seed + level.id)
    private val dungeonGenerator = DungeonGenerator(level.sizeX, level.sizeY, irng)
    private lateinit var dungeonLayout: Array<out CharArray>

    private val tilesetTypes: List<TilesetType> = listOf(
        TilesetType.DEFAULT_DUNGEON, TilesetType.CAVES_LIMIT_CONNECTIVITY, TilesetType.CORNER_CAVES,
        // Boss rooms, mines, rocky maps
//        TilesetType.HORIZONTAL_CORRIDORS_B, TilesetType.HORIZONTAL_CORRIDORS_C,
//        TilesetType.OPEN_AREAS,
//        TilesetType.REFERENCE_CAVES,
        TilesetType.MAZE_A, TilesetType.MAZE_B,
//        TilesetType.ROOMS_AND_CORRIDORS_A,
        TilesetType.ROOMS_AND_CORRIDORS_B,
        // Caves
//        TilesetType.ROUND_ROOMS_DIAGONAL_CORRIDORS,
        TilesetType.SIMPLE_CAVES
    )

    fun generateDungeon() {
        val tilesetType = tilesetTypes[level.randomGenerator.nextInt(0, tilesetTypes.size)]
        dungeonLayout = dungeonGenerator.addDoors(100, true)
            .generate(tilesetType)
        GlobalData.registerData(GlobalDataType.CHANGELEVEL, tilesetType.name)

        // dungeon stairs coordinates can be retrieved with those values
        dungeonGenerator.stairsUp
        dungeonGenerator.stairsDown
    }

    fun setWalls(map: List<List<MutableList<Entity>>>, wall: KClass<Entity>) {
        for (y in 0 until level.sizeY) {
            for (x in 0 until level.sizeX) {
                if (dungeonLayout[y][x] == '#')
                    map[y][x].add(wall.createInstance())
            }
        }
    }

    fun setEntrances(map: List<List<MutableList<Entity>>>, down: KClass<out Entity>, up: KClass<out Entity>) {
        map[dungeonGenerator.stairsDown.y][dungeonGenerator.stairsDown.x].add(down.createInstance())
        map[dungeonGenerator.stairsUp.y][dungeonGenerator.stairsUp.x].add(up.createInstance())
    }
}