package com.neutrino.game.domain.use_case.map

import com.neutrino.game.Constants.RandomGenerator
import com.neutrino.game.domain.model.entities.DungeonFloor
import com.neutrino.game.domain.model.entities.DungeonGrass
import com.neutrino.game.domain.model.entities.DungeonWall
import com.neutrino.game.domain.model.entities.Grass
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.map.Level

/**
 * Class used for map generation
 */
class GenerateMap(
    private val level: Level
) {
    private val squidGeneration = SquidGeneration(level.sizeX, level.sizeY)

    val map: List<List<MutableList<Entity>>> = List(level.sizeY) {
        List(level.sizeX) {
            ArrayList<Entity>()
        }
    }

    /**
     * Generates the map
     */
    operator fun invoke(): List<List<MutableList<Entity>>> {
        squidGeneration.generateDungeon()
        squidGeneration.setDungeonWalls(map)
        addEntities(DungeonFloor().javaClass, 1f)
        addEntities(DungeonGrass().javaClass, 0.3f)
        addEntities(Grass().javaClass, 0.3f, listOf(DungeonGrass().javaClass))

        return map
    }

    /**
     * Adds entities to the map with a certain probability
     * Supports restrictions, that allow generation only if one of required entities exists underneath
     */
    private fun addEntities(entity: Class<Entity>, probability: Float, requiredUnderneath: List<Class<Entity>> = listOf(), excludedUnderneath: List<Class<Entity>> = listOf()) {
        for (y in 0 until level.sizeY) {
            for (x in 0 until level.sizeX) {
                var allowGeneration = true
                if (map[y][x].isNotEmpty() && !map[y][x][0].allowOnTop)
                    allowGeneration = false

                if (requiredUnderneath.isNotEmpty()) {
                    allowGeneration = false
                    for (mapEntity in map[y][x]) {
                        if (!mapEntity.allowOnTop) {
                            allowGeneration = false
                            break
                        }
                        var excluded = false
                        for (excludedEntity in excludedUnderneath) {
                            if (mapEntity.javaClass == excludedEntity) {
                                excluded = true
                                break
                            }
                        }
                        if (excluded)
                            break
                        for (requiredEntity in requiredUnderneath) {
                            if (mapEntity.javaClass == requiredEntity) {
                                allowGeneration = true
                                break
                            }
                        }
                    }
                }

                if(allowGeneration && RandomGenerator.nextFloat() < probability)
                    map[y][x].add(entity.constructors[0].newInstance() as Entity)
            }
        }
    }
}