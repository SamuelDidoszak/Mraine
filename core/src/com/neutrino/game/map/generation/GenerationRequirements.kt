package com.neutrino.game.map.generation

import com.neutrino.game.entities.Entities
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.util.EntityId
import com.neutrino.game.util.EntityName

object GenerationRequirements {

    private val entityDefaults: HashMap<EntityId, List<EntityPositionRequirement>> = HashMap()
    private val identityDefaults: HashMap<Identity, List<EntityPositionRequirement>> = HashMap()
    private val others: HashMap<String, List<EntityPositionRequirement>> = HashMap()

    fun add(entity: EntityId, requirements: List<EntityPositionRequirement>) {
        entityDefaults[entity] = requirements
    }

    fun add(entity: EntityName, requirements: List<EntityPositionRequirement>) {
        entityDefaults[Entities.getId(entity)] = requirements
    }

    fun add(identity: Identity, requirements: List<EntityPositionRequirement>) {
        identityDefaults[identity] = requirements
    }

    fun addOther(ruleName: String, requirements: List<EntityPositionRequirement>) {
        others[ruleName] = requirements
    }

    fun get(entity: EntityName): List<EntityPositionRequirement> {
        return entityDefaults[Entities.getId(entity)]!!
    }

    fun get(entity: EntityId): List<EntityPositionRequirement> {
        return entityDefaults[entity]!!
    }

    fun get(identity: Identity): List<EntityPositionRequirement> {
        return identityDefaults[identity]!!
    }

    fun getOther(ruleName: String): List<EntityPositionRequirement> {
        return others[ruleName]!!
    }

    fun getEntities(): Set<EntityId> {
        return entityDefaults.keys
    }

    fun getIdentities(): Set<Identity> {
        return identityDefaults.keys
    }

    fun getOthers(): Set<String> {
        return others.keys
    }
}