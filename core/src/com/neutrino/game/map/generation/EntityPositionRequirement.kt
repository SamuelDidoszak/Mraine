package com.neutrino.game.map.generation

import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.map.generation.util.NameOrIdentity
import com.neutrino.game.util.EntityName
import kotlin.reflect.KClass

class EntityPositionRequirement {
    var requirementType: EntityPositionRequirementType
    private var requiredEntity: NameOrIdentity? = null
    var requirementList: List<Pair<Int, NameOrIdentity>>

    /** Use this constructor to interpret next values as grouped with requirementType group type */
    constructor(requirementType: EntityPositionRequirementType) {
        this.requirementType = requirementType
        this.requirementList = listOf()
    }

    constructor(requirementType: EntityPositionRequirementType,
                requiredEntity: NameOrIdentity, list: List<Int>) {
        this.requirementType = requirementType
        this.requiredEntity = requiredEntity
        requirementList = list.map { Pair(it, requiredEntity) }
    }

    constructor(requirementType: EntityPositionRequirementType,
                requiredEntity: EntityName, list: List<Int>): this(
                    requirementType, NameOrIdentity(requiredEntity), list)

    constructor(requirementType: EntityPositionRequirementType,
                requiredIdentity: Identity, list: List<Int>): this(
                    requirementType, NameOrIdentity(requiredIdentity::class), list)

    constructor(requirementType: EntityPositionRequirementType,
                requiredEntity: KClass<out Identity>, list: List<Int>) {
        this.requirementType = requirementType
        this.requiredEntity = NameOrIdentity(requiredEntity)
        requirementList = list.map { Pair(it, this.requiredEntity!!) }
    }

    constructor(requirementType: EntityPositionRequirementType, list: List<Pair<Int, NameOrIdentity>>) {
        this.requirementType = requirementType
        requirementList = list
    }

    override fun toString(): String {
        val builder = StringBuilder(50)
        builder.append("EntityPositionRequirement(EntityPositionRequirementType.${requirementType.name}")
        if (requiredEntity == null && requirementList.isEmpty())
            return builder.append(")").toString()
        if (requiredEntity?.identity != null)
            builder.append(", Identity." + requiredEntity!!.identity!!.simpleName + "(), ")
        else if (requiredEntity?.id != null)
            builder.append(", \"" + requiredEntity!!.getEntityName()!! + "\", ")
        builder.append("listOf(")
        requirementList.map { it.first }.forEach {
            builder.append("$it, ")
        }
        builder.delete(builder.length - 2, builder.length)
        builder.append("))")
        return builder.toString()
    }
}

enum class EntityPositionRequirementType {
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
