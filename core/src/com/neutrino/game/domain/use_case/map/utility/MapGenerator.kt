package com.neutrino.game.domain.use_case.map.utility

import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.map.level.Chunk
import com.neutrino.game.domain.model.map.TagInterpretation
import com.neutrino.game.util.isSuper
import com.neutrino.game.util.lessThanDelta
import kotlin.math.roundToInt
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

abstract class MapGenerator(val chunk: Chunk, val interpretedTags: TagInterpretation) {

    /**
     * Generates the map
     */
    abstract fun generate(): List<List<MutableList<Entity>>>

    val map: List<List<MutableList<Entity>>> = List(chunk.sizeY) {
        List(chunk.sizeX) {
            ArrayList<Entity>()
        }
    }

    fun addEntities(entity: KClass<out Entity>, entityPositionRequirementList: List<EntityPositionRequirement>, probability: Float,
                            replaceUnderneath: Boolean = false, assertAmount: Boolean = false) {
        val fulfillingTileList: MutableList<Pair<Int, Int>> = ArrayList()
        for (y in 0 until chunk.sizeY) {
            for (x in 0 until chunk.sizeX) {
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
                                if (x == 0 || y == chunk.sizeY - 1)
                                    break
                                if (!checkMapForEntity(y + 1, x - 1, pair.second))
                                    entityUnder = false
                            }
                            2 -> {
                                if (y == chunk.sizeY - 1)
                                    break
                                if (!checkMapForEntity(y + 1, x, pair.second))
                                    entityUnder = false
                            }
                            3 -> {
                                if (x == chunk.sizeY - 1 || y == chunk.sizeY - 1)
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
                                if (x == chunk.sizeX - 1)
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
                                if (x == chunk.sizeX - 1 || y == 0)
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
                else if (generationAllowed && chunk.randomGenerator.nextFloat().lessThanDelta(probability)) {
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
                val index = chunk.randomGenerator.nextInt(fulfillingTileList.size)
                map[fulfillingTileList[index].first][fulfillingTileList[index].second].add(entity.createInstance())
                fulfillingTileList.removeAt(index)
                generatedAmount++
            }
        }
    }

    fun checkMapForEntity(y: Int, x: Int, requiredEntity: KClass<out Entity>): Boolean {
        for (mapEntity in map[y][x]) {
            if (mapEntity::class == requiredEntity || mapEntity isSuper requiredEntity)
                return true
        }
        return false
    }
}