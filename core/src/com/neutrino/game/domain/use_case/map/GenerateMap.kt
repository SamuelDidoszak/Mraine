package com.neutrino.game.domain.use_case.map

import com.neutrino.game.Constants.RandomGenerator
import com.neutrino.game.domain.model.entities.*
import com.neutrino.game.domain.model.entities.containers.*
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.model.map.TagInterpretation
import com.neutrino.game.domain.use_case.map.utility.EntityPositionRequirement
import com.neutrino.game.domain.use_case.map.utility.EntityPositionRequirementType
import com.neutrino.game.lessThanDelta
import kotlin.math.roundToInt
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
        val interpretedTags = TagInterpretation(level.tagList)
        val difficultyModifier = kotlin.math.abs(level.zPosition)
        interpretedTags.generationParams.difficulty += difficultyModifier / 4

        squidGeneration.generateDungeon()
        squidGeneration.setWalls(map, interpretedTags.entityParams.wall)
        addEntities(interpretedTags.entityParams.floor, listOf(),1f)

        addEntities(StonePillar::class as KClass<Entity>, listOf(
            EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(7, 8, 9)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(1, 2, 3, 4, 6))
        ), 6f, assertAmount = true)

        addEntities(StonePillar::class as KClass<Entity>, listOf(
            EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(1, 2, 3)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(4, 6, 7, 8, 9))
        ), 2f, assertAmount = true)

        addEntities(WoodenDoor::class as KClass<Entity>, listOf(
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(1, 2, 3, 8)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(4, 7, 6, 9)),
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(7, 8, 9, 2)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(1, 4, 3, 6)),
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(4, 3, 6, 9)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(1, 2, 7, 8)),
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(1, 4, 7, 6)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(2, 3, 8, 9)),
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(2, 8)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(4, 9, 6, 3)),
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(2, 8)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(6, 1, 4, 7)),
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(4, 6)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(8, 1, 2, 3)),
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(4, 6)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(2, 7, 8, 9))
        ), 10f, assertAmount = true)

        addEntities(
            WoodenDoorArched::class as KClass<Entity>, listOf(
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(1, 2, 3, 8)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(4, 7, 6, 9)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(7, 8, 9, 2)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(1, 4, 3, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(4, 3, 6, 9)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(1, 2, 7, 8)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(1, 4, 7, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(2, 3, 8, 9)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(2, 8)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(4, 9, 6, 3)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(2, 8)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(6, 1, 4, 7)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(4, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(8, 1, 2, 3)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(4, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(2, 7, 8, 9))
        ), 8f, assertAmount = true)

        addEntities(
            CrateDoor::class as KClass<Entity>, listOf(
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(4, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(8, 1, 2, 3)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, interpretedTags.entityParams.wall, listOf(4, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, interpretedTags.entityParams.wall, listOf(2, 7, 8, 9)),
            ), 5f, assertAmount = true)


        addEntitiesNearWall(Barrel::class as KClass<Entity>, 0.0085f, false)
        addEntitiesNearWall(CrateSmall::class as KClass<Entity>, 0.005f, false)

        addEntities(CrateBigger::class as KClass<Entity>, listOf(
            EntityPositionRequirement(EntityPositionRequirementType.AND, DungeonWall::class as KClass<Entity>, listOf(7, 8, 9)),
            EntityPositionRequirement(EntityPositionRequirementType.NAND, DungeonWall::class as KClass<Entity>, listOf(4, 6)),
            EntityPositionRequirement(EntityPositionRequirementType.NAND, CrateBigger::class as KClass<Entity>, listOf(4)),
        ), 0.01f)

        addEntities(WoodenChest::class as KClass<Entity>, listOf(
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, DungeonWall::class as KClass<Entity>, listOf(1, 4, 7)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, DungeonWall::class as KClass<Entity>, listOf(2, 3, 6, 8, 9)),
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, DungeonWall::class as KClass<Entity>, listOf(7, 8, 9)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, DungeonWall::class as KClass<Entity>, listOf(4, 1, 2, 3, 6)),
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, DungeonWall::class as KClass<Entity>, listOf(9, 6, 3)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, DungeonWall::class as KClass<Entity>, listOf(7, 8, 4, 1, 2)),
            EntityPositionRequirement(EntityPositionRequirementType.AND),
            EntityPositionRequirement(EntityPositionRequirementType.AND, DungeonWall::class as KClass<Entity>, listOf(1, 2, 3)),
            EntityPositionRequirement(EntityPositionRequirementType.NOR, DungeonWall::class as KClass<Entity>, listOf(4, 7, 8, 9, 6)),
        ), 3f, assertAmount = true)

        addEntitiesNearWall(ClayPot::class as KClass<Entity>, 0.01f, false)
        addEntitiesNearWall(ClayPotMultiple::class as KClass<Entity>, 0.005f, false)

        GenerateItems(map, interpretedTags.itemList, interpretedTags.generationParams)()

        return map
    }

    private fun addEntitiesNearWall(entity: KClass<Entity>, probability: Float, canBlockPassage: Boolean = true) {
        var requirementList = listOf(
            EntityPositionRequirement(EntityPositionRequirementType.OR, DungeonWall::class as KClass<Entity>,
                listOf(2, 4, 6, 8))
        )

        if (!canBlockPassage) {
            requirementList = requirementList.plus(listOf(
                EntityPositionRequirement(EntityPositionRequirementType.NAND, DungeonWall::class as KClass<Entity>,
                    listOf(4, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NAND, DungeonWall::class as KClass<Entity>,
                    listOf(2, 8))
            ))
        }

        addEntities(entity, requirementList, probability)
    }

    private fun addEntities(entity: KClass<Entity>, entityPositionRequirementList: List<EntityPositionRequirement>, probability: Float,
                            replaceUnderneath: Boolean = false, assertAmount: Boolean = false) {
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
                var groupRequirementType: EntityPositionRequirementType? = null
                for (requirement in entityPositionRequirementList) {
                    // Group initiator
                    if (requirement.requirementList.isEmpty()) {
                        if (when(groupRequirementType) {
                            EntityPositionRequirementType.AND -> generationAllowed
                            EntityPositionRequirementType.NAND -> generationAllowed
                            EntityPositionRequirementType.NOR -> generationAllowed
                            EntityPositionRequirementType.OR, null -> false
                        })
                            break

                        groupRequirementType = requirement.requirementType
                        generationAllowed = true
                        continue
                    }
                    // Iterate until the next group
                    if (!generationAllowed)
                        continue

                    for (pair in requirement.requirementList) {
                        var entityUnder = true
                        when (pair.first) {
                            1 -> {
                                if (x == 0 || y == level.sizeY - 1)
                                    break
                                if (!checkMapForEntity(y + 1, x - 1, pair.second))
                                    entityUnder = false
                            }
                            2 -> {
                                if (y == level.sizeY - 1)
                                    break
                                if (!checkMapForEntity(y + 1, x, pair.second))
                                    entityUnder = false
                            }
                            3 -> {
                                if (x == level.sizeY - 1 || y == level.sizeY - 1)
                                    break
                                if (!checkMapForEntity(y + 1, x + 1, pair.second))
                                    entityUnder = false
                            }
                            4 -> {
                                if (x == 0)
                                    break
                                if (!checkMapForEntity(y, x - 1, pair.second))
                                    entityUnder = false
                            }
                            5 -> {
                                if (!checkMapForEntity(y, x, pair.second))
                                    entityUnder = false
                            }
                            6 -> {
                                if (x == level.sizeX - 1)
                                    break
                                if (!checkMapForEntity(y, x + 1, pair.second))
                                    entityUnder = false
                            }
                            7 -> {
                                if (x == 0 || y == 0)
                                    break
                                if (!checkMapForEntity(y - 1, x - 1, pair.second))
                                    entityUnder = false
                            }
                            8 -> {
                                if (y == 0)
                                    break
                                if (!checkMapForEntity(y - 1, x, pair.second))
                                    entityUnder = false
                            }
                            9 -> {
                                if (x == level.sizeX - 1 || y == 0)
                                    break
                                if (!checkMapForEntity(y - 1, x + 1, pair.second))
                                    entityUnder = false
                            }
                        }
                        when (requirement.requirementType) {
                            EntityPositionRequirementType.AND -> {
                                if (!entityUnder) {
                                    generationAllowed = false
                                    break
                                }
                            }
                            EntityPositionRequirementType.NAND -> {
                                if (!entityUnder) {
                                    generationAllowed = true
                                    break
                                } else
                                    generationAllowed = false
                            }
                            EntityPositionRequirementType.OR -> {
                                if (entityUnder) {
                                    generationAllowed = true
                                    break
                                } else
                                    generationAllowed = false
                            }
                            EntityPositionRequirementType.NOR -> {
                                if (entityUnder) {
                                    generationAllowed = false
                                    break
                                }
                            }
                        }
                    }
                    when (groupRequirementType) {
                        // not grouped, all of the requirements have to be passed
                        null -> {
                            if (!generationAllowed)
                                continue
                        }
                        EntityPositionRequirementType.AND -> {
                            if (!generationAllowed)
                                continue
                        }
                        EntityPositionRequirementType.NAND -> {
                            if (!generationAllowed) {
                                generationAllowed = true
                                break
                            }
                        }
                        EntityPositionRequirementType.OR -> {
                            if (generationAllowed)
                                break

                            if (requirement != entityPositionRequirementList.last())
                                generationAllowed = true
                        }
                        EntityPositionRequirementType.NOR -> {
                            if (generationAllowed) {
                                generationAllowed = false
                                continue
                            }

                            generationAllowed = true
                        }
                    }
                }
                // Add this tile to the list and generate later
                if (assertAmount && generationAllowed) {
                    fulfillingTileList.add(Pair(y, x))
                }

                // Generate entity if allowed with a probability
                else if (generationAllowed && RandomGenerator.nextFloat().lessThanDelta(probability)) {
                    if (replaceUnderneath)
                        map[y][x].removeAll { true }
                    // Assert that the entity wasn't added already
                    if (!checkMapForEntity(y, x, entity))
                        map[y][x].add(entity.createInstance())
                }
            }
        }
        // generate certain amount of items
        // treat probability as amount
        if (assertAmount) {
            var generatedAmount = 0
            val max = if (probability >= fulfillingTileList.size) fulfillingTileList.size else probability.roundToInt()
            while (generatedAmount < max) {
                val index = RandomGenerator.nextInt(fulfillingTileList.size)
                map[fulfillingTileList[index].first][fulfillingTileList[index].second].add(entity.createInstance())
                fulfillingTileList.removeAt(index)
                generatedAmount++
            }
        }
    }

    private fun checkMapForEntity(y: Int, x: Int, requiredEntity: KClass<Entity>): Boolean {
        for (mapEntity in map[y][x]) {
            if (mapEntity::class == requiredEntity)
                return true
        }
        return false
    }
}