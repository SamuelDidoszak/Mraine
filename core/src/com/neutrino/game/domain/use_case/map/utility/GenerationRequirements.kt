package com.neutrino.game.domain.use_case.map.utility

import com.neutrino.game.domain.model.entities.*
import com.neutrino.game.domain.model.entities.containers.WoodenChest
import com.neutrino.game.domain.model.entities.lightSources.StandingTorch
import com.neutrino.game.domain.model.entities.lightSources.Torch
import kotlin.reflect.KClass

object GenerationRequirements {
    private val entityDefaults: HashMap<KClass<out Entity>, List<EntityPositionRequirement>> = HashMap()
    
    fun addDefaults(entity: KClass<out Entity>, entityPositionRequirementList: List<EntityPositionRequirement>) {
        entityDefaults[entity] = entityPositionRequirementList
    }
    
    fun getDefaults(entity: KClass<out Entity>): List<EntityPositionRequirement> {
        return entityDefaults[entity]!!
    }

    fun requirementNearWall(canBlockPassage: Boolean = false): List<EntityPositionRequirement> {
        var requirementList = listOf(
            EntityPositionRequirement(EntityPositionRequirementType.OR, Wall::class,
                listOf(2, 4, 6, 8))
        )

        if (!canBlockPassage) {
            requirementList = requirementList.plus(listOf(
                EntityPositionRequirement(EntityPositionRequirementType.NAND, Wall::class,
                    listOf(4, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NAND, Wall::class,
                    listOf(2, 8))
            ))
        }
        return requirementList
    }
    
    init {
        addDefaults(
            WoodenDoor::class, listOf(
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(1, 2, 3, 8)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(4, 7, 6, 9)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(7, 8, 9, 2)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(1, 4, 3, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(4, 3, 6, 9)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(1, 2, 7, 8)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(1, 4, 7, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(2, 3, 8, 9)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(2, 8)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(4, 9, 6, 3)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(2, 8)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(6, 1, 4, 7)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(4, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(8, 1, 2, 3)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(4, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(2, 7, 8, 9))
            )
        )
        
        addDefaults(WoodenDoorArched::class, getDefaults(WoodenDoor::class))
        
        addDefaults(
            CrateDoor::class, listOf(
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(4, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(8, 1, 2, 3)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(4, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(2, 7, 8, 9)),
            )
        )
        
        addDefaults(
            Torch::class, listOf(
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(7, 8, 9)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(1, 2, 3)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(1, 4, 7)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(3, 6, 9)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(3, 6, 9)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(1, 4, 7))
            )
        )
        
        addDefaults(
            StandingTorch::class, listOf(
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(1, 2, 3, 4, 6, 7, 8, 9))
            )
        )

        addDefaults(
            WoodenChest::class, listOf(
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(1, 4, 7)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(2, 3, 6, 8, 9)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(7, 8, 9)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(4, 1, 2, 3, 6)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(9, 6, 3)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(7, 8, 4, 1, 2)),
                EntityPositionRequirement(EntityPositionRequirementType.AND),
                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(1, 2, 3)),
                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(4, 7, 8, 9, 6)),
            )
        )
    }
}