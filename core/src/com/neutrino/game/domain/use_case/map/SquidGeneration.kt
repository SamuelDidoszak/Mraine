package com.neutrino.game.domain.use_case.map

import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.Constants.IsSeeded
import com.neutrino.game.Constants.RandomGenerator
import com.neutrino.game.Constants.Seed
import com.neutrino.game.domain.model.entities.utility.Entity
import squidpony.squidgrid.mapping.DungeonGenerator
import squidpony.squidgrid.mapping.styled.TilesetType
import squidpony.squidmath.GWTRNG
import squidpony.squidmath.IRNG
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


class SquidGeneration (
    val mapX: Int,
    val mapY: Int
) {
    private val irng: IRNG = if (IsSeeded) GWTRNG(Seed) else GWTRNG()
    private val dungeonGenerator = DungeonGenerator(mapX, mapY, irng)
    private lateinit var dungeonLayout: Array<out CharArray>

    private val tilesetTypes: List<TilesetType> = listOf(
        TilesetType.DEFAULT_DUNGEON, TilesetType.CAVES_LIMIT_CONNECTIVITY, TilesetType.CORNER_CAVES,
        TilesetType.HORIZONTAL_CORRIDORS_B, TilesetType.HORIZONTAL_CORRIDORS_C,
        TilesetType.OPEN_AREAS, TilesetType.REFERENCE_CAVES,
        TilesetType.MAZE_A, TilesetType.MAZE_B,
        TilesetType.ROOMS_AND_CORRIDORS_A, TilesetType.ROOMS_AND_CORRIDORS_B,
        TilesetType.ROUND_ROOMS_DIAGONAL_CORRIDORS, TilesetType.SIMPLE_CAVES
    )

    fun generateDungeon() {
        val tilesetType = tilesetTypes[RandomGenerator.nextInt(0, tilesetTypes.size)]
        dungeonLayout = dungeonGenerator.addDoors(100, true)
            .generate(tilesetType)
        GlobalData.registerData(GlobalDataType.CHANGELEVEL, tilesetType.name)

        // dungeon stairs coordinates can be retrieved with those values
        dungeonGenerator.stairsUp
        dungeonGenerator.stairsDown
    }

    fun setWalls(map: List<List<MutableList<Entity>>>, wall: KClass<Entity>) {
        for (y in 0 until mapY) {
            for (x in 0 until mapX) {
                if (dungeonLayout[y][x] == '#')
                    map[y][x].add(wall.createInstance())
            }
        }
    }
}