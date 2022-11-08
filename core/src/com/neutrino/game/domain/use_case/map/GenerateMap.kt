package com.neutrino.game.domain.use_case.map

import com.neutrino.game.Constants.RandomGenerator
import com.neutrino.game.domain.model.entities.*
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.ItemEntity
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.edible.SmallHealingPotion
import com.neutrino.game.domain.model.items.equipment.Knife
import com.neutrino.game.domain.model.items.items.Gold
import com.neutrino.game.domain.model.items.scrolls.ScrollOfDefence
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.use_case.map.utility.EntityPositionRequirement
import com.neutrino.game.domain.use_case.map.utility.EntityPositionRequirementType
import com.neutrino.game.lessThanDelta
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
        addEntitiesNearWall(Barrel::class as KClass<Entity>, 0.01f, false)
        addEntitiesNearWall(CrateSmall::class as KClass<Entity>, 0.0075f)

        addEntities(CrateBigger::class as KClass<Entity>, listOf(
            EntityPositionRequirement(EntityPositionRequirementType.REQUIRED, DungeonWall::class as KClass<Entity>, listOf(7, 8, 9)),
            EntityPositionRequirement(EntityPositionRequirementType.FORBIDDEN, DungeonWall::class as KClass<Entity>, listOf(4, 6)),
            EntityPositionRequirement(EntityPositionRequirementType.FORBIDDEN, CrateBigger::class as KClass<Entity>, listOf(4)),
        ), 0.05f)

        val blockedTilesPercentage: Float = getBlockedTilesPercentage()
        addItems(Gold::class as KClass<Item>, 50f)
        addItems(Knife::class as KClass<Item>, 5f)
        addItems(SmallHealingPotion::class as KClass<Item>, 5f, blockedTilesPercentage)
        addItems(ScrollOfDefence::class as KClass<Item>, 0.3f)

        return map
    }

    private fun addEntitiesNearWall(entity: KClass<Entity>, probability: Float, canBlockPassage: Boolean = true) {
        var requirementList = listOf(
            EntityPositionRequirement(EntityPositionRequirementType.OPTIONAL, DungeonWall::class as KClass<Entity>,
                listOf(2, 4, 6, 8))
        )

        if (!canBlockPassage) {
            requirementList = requirementList.plus(
                EntityPositionRequirement(EntityPositionRequirementType.FORBIDDEN, DungeonWall::class as KClass<Entity>,
                    listOf(4, 6)))
            requirementList = requirementList.plus(
                EntityPositionRequirement(EntityPositionRequirementType.FORBIDDEN, DungeonWall::class as KClass<Entity>,
                    listOf(2, 8)))
        }

        addEntities(entity, requirementList, probability)
    }

    private fun addEntities(entity: KClass<Entity>, entityPositionRequirementList: List<EntityPositionRequirement>, probability: Float,
                            replaceUnderneath: Boolean = false, assertAmount: Boolean = false): MutableList<Pair<Int, Int>> {
        val fulfillingTileList: MutableList<Pair<Int, Int>> = ArrayList()
        for (y in 0 until level.sizeY) {
            for (x in 0 until level.sizeX) {
                var generationAllowed = true
                for (mapEntity in map[y][x]) {
                    if (!mapEntity.allowOnTop) {
                        generationAllowed = false
                        break
                    }
                }
                if (!generationAllowed)
                    continue
                // Interpret requirements

                // Variable needed to interpret remaining requirements as grouped
                var currentRequirementType: EntityPositionRequirementType? = null
                for (requirement in entityPositionRequirementList) {
                    if (requirement.requirementList.isEmpty())
                        currentRequirementType = requirement.requirementType

                    for (pair in requirement.requirementList) {
                        var requirementFulfilled = true
                        when (pair.first) {
                            1 -> {
                                if (x == 0 || y == level.sizeY - 1)
                                    break
                                if (!checkMapForEntity(y + 1, x - 1, pair.second))
                                    requirementFulfilled = false
                            }
                            2 -> {
                                if (y == level.sizeY - 1)
                                    break
                                if (!checkMapForEntity(y + 1, x, pair.second))
                                    requirementFulfilled = false
                            }
                            3 -> {
                                if (x == level.sizeY - 1 || y == level.sizeY - 1)
                                    break
                                if (!checkMapForEntity(y + 1, x + 1, pair.second))
                                    requirementFulfilled = false
                            }
                            4 -> {
                                if (x == 0)
                                    break
                                if (!checkMapForEntity(y, x - 1, pair.second))
                                    requirementFulfilled = false
                            }
                            5 -> {
                                if (!checkMapForEntity(y, x, pair.second))
                                    requirementFulfilled = false
                            }
                            6 -> {
                                if (x == level.sizeX - 1)
                                    break
                                if (!checkMapForEntity(y, x + 1, pair.second))
                                    requirementFulfilled = false
                            }
                            7 -> {
                                if (x == 0 || y == 0)
                                    break
                                if (!checkMapForEntity(y - 1, x - 1, pair.second))
                                    requirementFulfilled = false
                            }
                            8 -> {
                                if (y == 0)
                                    break
                                if (!checkMapForEntity(y - 1, x, pair.second))
                                    requirementFulfilled = false
                            }
                            9 -> {
                                if (x == level.sizeX - 1 || y == 0)
                                    break
                                if (!checkMapForEntity(y - 1, x + 1, pair.second))
                                    requirementFulfilled = false
                            }
                        }
                        when (requirement.requirementType) {
                            EntityPositionRequirementType.REQUIRED -> {
                                if (!requirementFulfilled) {
                                    generationAllowed = false
                                    break
                                }
                            }
                            EntityPositionRequirementType.FORBIDDEN -> {
                                if (!requirementFulfilled) {
                                    generationAllowed = true
                                    break
                                } else
                                    generationAllowed = false
                            }
                            EntityPositionRequirementType.OPTIONAL -> {
                                if (requirementFulfilled) {
                                    generationAllowed = true
                                    break
                                } else
                                    generationAllowed = false
                            }
                        }
                    }
                    if (!generationAllowed)
                        break
                }
                // Generate entity if allowed with a probability
                if (generationAllowed && RandomGenerator.nextFloat().lessThanDelta(probability)) {
                    if (replaceUnderneath)
                        map[y][x].removeAll { true }
                    // Assert that the entity wasn't added already
                    if (!checkMapForEntity(y, x, entity))
                        map[y][x].add(entity.createInstance())
                }
            }
        }
        return fulfillingTileList
    }

    private fun checkMapForEntity(y: Int, x: Int, requiredEntity: KClass<Entity>): Boolean {
        for (mapEntity in map[y][x]) {
            if (mapEntity::class == requiredEntity)
                return true
        }
        return false
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
     * Returns the percentage of tiles with .allowOnTop set to false
     * Useful for making sure, that a certain amount of items will appear in chunk
     */
    private fun getBlockedTilesPercentage(): Float {
        var blockedAmount = 0
        for (y in 0 until level.sizeY) {
            for (x in 0 until level.sizeX) {
                for (mapEntity in map[y][x]) {
                    if (!mapEntity.allowOnTop) {
                        blockedAmount++
                        break
                    }
                }
            }
        }
        return blockedAmount / (level.sizeY * level.sizeX).toFloat()
    }

    /** Returns the amount of certain items spawned */
    private fun checkSpawnedItemAmount(item: KClass<Item>): Int {
        var amount = 0
        for (y in 0 until level.sizeY) {
            for (x in 0 until level.sizeX) {
                for (mapEntity in map[y][x]) {
                    if (mapEntity is ItemEntity && mapEntity.item::class == item) {
                        amount++
                        break
                    }
                }
            }
        }
        return amount
    }

    /**
     * Adds items to the map with a certain probability
     * @param amount how many items per 100x100 chunk
     * @param includeBlocked if the value is set, percentage increases to account for blocked tiles. Pass value calculated from getBlockedTilesPercentage()
     * TODO add a richness value to some tiles and generate items based on that
     */
    private fun addItems(item: KClass<Item>, amount: Float, includeBlocked: Float? = null) {
        var probability = amount * 0.0001
        if (includeBlocked != null)
            probability += probability * includeBlocked

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