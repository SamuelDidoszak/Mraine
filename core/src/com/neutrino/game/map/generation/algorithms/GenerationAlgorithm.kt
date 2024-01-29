package com.neutrino.game.map.generation.algorithms

import com.neutrino.game.entities.Entities
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.map.generation.*
import com.neutrino.game.map.generation.util.EntityGenerationParams
import com.neutrino.game.map.generation.util.GenerationParams
import com.neutrino.game.map.generation.util.ModifyMap
import com.neutrino.game.map.generation.util.NameOrIdentity
import com.neutrino.game.util.EntityId
import com.neutrino.game.util.EntityName
import com.neutrino.game.util.id
import com.neutrino.game.util.lessThanDelta
import kotlin.math.roundToInt

abstract class GenerationAlgorithm(
    val params: GenerationParams,
    var sizeX: Int = params.map[0].size,
    var sizeY: Int = params.map.size,
    val modifyBaseMap: ModifyMap? = null) {

    internal val map: List<List<MutableList<Entity>>> = initializeMap()
    internal val entities = ArrayList<EntityGenerationParams>()
    internal val defaultBlockMap = List<List<Boolean>>(sizeY) {List<Boolean>(sizeX) {true} }

    /**
     * Invokes the generation specific to this GenerationAlgorithm
     */
    abstract fun generate(
        tileset: Tileset = params.interpretedTags.tilesets[0]
    ): GenerationAlgorithm

    /**
     * Invokes both the generation specific to this GenerationAlgorithm and generates all of entities provided
     */
    fun generateAll(tileset: Tileset = params.interpretedTags.tilesets[0]): GenerationAlgorithm {
        generate(tileset)
        generateEntities()
        return this
    }

    open fun generateEntities(): GenerationAlgorithm {
        for (entityParams in entities) {
            addEntities(entityParams.id, entityParams.requirements, entityParams.amount, entityParams.asProbability, entityParams.replaceUnderneath)
        }
        return this
    }

    fun add(name: EntityName, amount: Float, asProbability: Boolean = false, replaceUnderneath: Boolean = false): GenerationAlgorithm {
        val id = Entities.getId(name)
        entities.add(EntityGenerationParams(id, GenerationRequirements.get(id), amount, asProbability, replaceUnderneath))
        return this
    }

    fun add(name: EntityName, entityRequirement: EntityName, amount: Float, asProbability: Boolean = false, replaceUnderneath: Boolean = false): GenerationAlgorithm {
        val id = Entities.getId(name)
        entities.add(EntityGenerationParams(id, GenerationRequirements.get(entityRequirement.id()), amount, asProbability, replaceUnderneath))
        return this
    }

    fun add(name: EntityName, isOther: Boolean, otherRequirement: String, amount: Float, asProbability: Boolean = false, replaceUnderneath: Boolean = false): GenerationAlgorithm {
        val id = Entities.getId(name)
        entities.add(EntityGenerationParams(id, GenerationRequirements.getOther(otherRequirement), amount, asProbability, replaceUnderneath))
        return this
    }

    fun add(name: EntityName, identityRequirement: Identity, amount: Float, asProbability: Boolean = false, replaceUnderneath: Boolean = false): GenerationAlgorithm {
        val id = Entities.getId(name)
        entities.add(EntityGenerationParams(id, GenerationRequirements.get(identityRequirement), amount, asProbability, replaceUnderneath))
        return this
    }

    fun add(name: EntityName, requirements: List<EntityPositionRequirement>, amount: Float, asProbability: Boolean = false, replaceUnderneath: Boolean = false): GenerationAlgorithm {
        val id = Entities.getId(name)
        entities.add(EntityGenerationParams(id, requirements, amount, asProbability, replaceUnderneath))
        return this
    }

    private fun initializeMap(): List<List<MutableList<Entity>>> {
        if (sizeX != params.map[0].size || sizeY != params.map.size) {
            if (modifyBaseMap == null)
                return getEmptyMap(sizeX, sizeY)
            else
                TODO("Sublist of the OG map")
        }
        return params.map
    }

    private fun getEmptyMap(sizeX: Int, sizeY: Int): List<List<MutableList<Entity>>> {
        val list = arrayListOf<ArrayList<MutableList<Entity>>>()
        for (y in 0 until sizeY) {
            list.add(arrayListOf())
            for (x in 0 until sizeX) {
                list[y].add(mutableListOf())
            }
        }
        return list
    }


    fun addEntities(entity: EntityId, entityPositionRequirementList: List<EntityPositionRequirement>, amount: Float,
                    asProbability: Boolean = false, replaceUnderneath: Boolean = false) {
        addEntities(0 until sizeY, 0 until sizeX, defaultBlockMap,
            entity, entityPositionRequirementList, amount, asProbability, replaceUnderneath)
    }

    fun addEntities(yIterator: IntRange, xIterator: IntRange, blockMap: List<List<Boolean>>, entity: EntityId, entityPositionRequirementList: List<EntityPositionRequirement>, amount: Float,
                    asProbability: Boolean = false, replaceUnderneath: Boolean = false) {
        val fulfillingTileList: MutableList<Pair<Int, Int>> = ArrayList()
        val entityChecker = NameOrIdentity(entity)
        for (y in yIterator) {
            for (x in xIterator) {
                if (!blockMap[y][x])
                    continue
                var generationAllowed = true
                for (mapEntity in map[y][x]) {
                    if (!mapEntity.get(MapParams::class)!!.allowOnTop) {
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
                                if (x == 0 || y == sizeY - 1)
                                    break
                                if (!checkMapForEntity(y + 1, x - 1, pair.second))
                                    entityUnder = false
                            }
                            2 -> {
                                if (y == sizeY - 1)
                                    break
                                if (!checkMapForEntity(y + 1, x, pair.second))
                                    entityUnder = false
                            }
                            3 -> {
                                if (x == sizeY - 1 || y == sizeY - 1)
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
                                if (x == sizeX - 1)
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
                                if (x == sizeX - 1 || y == 0)
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
                if (generationAllowed && !asProbability) {
                    fulfillingTileList.add(Pair(y, x))
                }

                // Generate entity if allowed with a probability
                else if (generationAllowed && params.rng.nextFloat().lessThanDelta(amount)) {
                    if (replaceUnderneath)
                        map[y][x].removeAll { true }
                    // Assert that the entity wasn't added already
                    if (!checkMapForEntity(y, x, entityChecker))
                        map[y][x].add(Entities.new(entity))
                }
            }
        }
        // generate certain amount of items
        // treat probability as amount
        if (!asProbability) {
            var generatedAmount = 0
            val max = if (amount >= fulfillingTileList.size) fulfillingTileList.size else amount.roundToInt()
            while (generatedAmount < max) {
                val index = params.rng.nextInt(fulfillingTileList.size)
                map[fulfillingTileList[index].first][fulfillingTileList[index].second].add(Entities.new(entity))
                fulfillingTileList.removeAt(index)
                generatedAmount++
            }
        }
    }

    fun checkMapForEntity(y: Int, x: Int, requiredEntity: NameOrIdentity): Boolean {
        for (mapEntity in map[y][x]) {
            if (requiredEntity.isSame(mapEntity))
                return true
        }
        return false
    }
}
