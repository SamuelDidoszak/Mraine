package com.neutrino.game.domain.use_case.map

import com.neutrino.game.Constants.RandomGenerator
import com.neutrino.game.domain.model.entities.DungeonFloor
import com.neutrino.game.domain.model.entities.DungeonGrass
import com.neutrino.game.domain.model.entities.Grass
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.ItemEntity
import com.neutrino.game.domain.model.items.Gold
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.Knife
import com.neutrino.game.domain.model.items.edible.SmallHealingPotion
import com.neutrino.game.domain.model.map.Level
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

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
        addEntities(DungeonFloor::class as KClass<Entity>, 1f)
        addEntities(DungeonGrass::class as KClass<Entity>, 0.3f)
        addEntities(Grass::class as KClass<Entity>, 0.3f, listOf(DungeonGrass::class as KClass<Entity>))
        addItems(Gold::class as KClass<Item>, 0.005f)
        addItems(Knife::class as KClass<Item>, 0.0005f)
        addItems(SmallHealingPotion::class as KClass<Item>, 0.001f)

        return map
    }

    /**
     * Adds entities to the map with a certain probability
     * Supports restrictions, that allow generation only if one of required entities exists underneath
     */
    private fun addEntities(entity: KClass<Entity>, probability: Float, requiredUnderneath: List<KClass<Entity>> = listOf(), excludedUnderneath: List<KClass<Entity>> = listOf()) {
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
                            if (mapEntity::class == excludedEntity) {
                                excluded = true
                                break
                            }
                        }
                        if (excluded)
                            break
                        for (requiredEntity in requiredUnderneath) {
                            if (mapEntity::class == requiredEntity) {
                                allowGeneration = true
                                break
                            }
                        }
                    }
                }

                if(allowGeneration && RandomGenerator.nextFloat() < probability)
                    map[y][x].add(entity.createInstance())
            }
        }
    }

    /**
     * Adds items to the map with a certain probability
     * TODO add a richness value to some tiles and generate items based on that
     */
    private fun addItems(item: KClass<Item>, probability: Float) {
        for (y in 0 until level.sizeY) {
            for (x in 0 until level.sizeX) {
                var allowGeneration = true
                if (map[y][x].isNotEmpty() && !map[y][x][0].allowOnTop)
                    allowGeneration = false

                if(allowGeneration && RandomGenerator.nextFloat() < probability)
                    map[y][x].add(ItemEntity(item.createInstance()) as Entity)
            }
        }
    }
}