package com.neutrino.game.domain.use_case.map.utility

import com.neutrino.game.domain.model.entities.utility.Entity
import kotlin.reflect.KClass

class EntityPositionRequirement {
    var requirementType: EntityPositionRequirementType
    private var requiredEntity: KClass<Entity>? = null
    var requirementList: List<Pair<Int, KClass<Entity>>>

    /** Use this constructor to interpret next values as grouped with requirementType group type */
    constructor(requirementType: EntityPositionRequirementType) {
        this.requirementType = requirementType
        this.requirementList = listOf()
    }

    constructor(requirementType: EntityPositionRequirementType,
                requiredEntity: KClass<Entity>, list: List<Int>) {
        this.requirementType = requirementType
        this.requiredEntity = requiredEntity
        requirementList = list.map { Pair(it, requiredEntity) }
    }

    constructor(requirementType: EntityPositionRequirementType, list: List<Pair<Int, KClass<Entity>>>) {
        this.requirementType = requirementType
        requirementList = list
    }
}

enum class EntityPositionRequirementType{
    /** All of the requirements have to be fulfilled for generation.
     * In a group: all of below requirements have to be fulfilled*/
    AND,
    /** Passed requirements cannot be fulfilled.
     * In a group: will not generate if below requirements were fulfilled*/
    NAND,
    /** Any of the requirements have to be fulfilled for generation.
     * In a group: any of below requirements have to be fulfilled*/
    OR,
    /** None of the requirements can be fulfilled.
     * In a group: none of below requirements can be fulfilled*/
    NOR,
}