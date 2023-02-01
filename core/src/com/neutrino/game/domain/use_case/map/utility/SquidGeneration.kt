package com.neutrino.game.domain.use_case.map.utility

import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.Wall
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

    fun generateDungeon(tilesetType: TilesetType) {
        dungeonLayout = dungeonGenerator.addDoors(100, true)
            .generate(tilesetType)
        GlobalData.registerData(GlobalDataType.CHANGELEVEL, tilesetType.name)
    }

    fun setWalls(map: List<List<MutableList<Entity>>>, wall: KClass<out Wall>) {
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