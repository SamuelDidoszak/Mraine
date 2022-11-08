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
    REQUIRED,
    FORBIDDEN,
    OPTIONAL
}