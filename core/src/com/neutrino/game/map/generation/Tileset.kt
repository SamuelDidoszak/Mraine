package com.neutrino.game.map.generation

import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.util.EntityId
import com.neutrino.game.util.EntityName
import com.neutrino.game.util.addInitial
import com.neutrino.game.util.id
import kotlin.random.Random

class Tileset() {

    private val entityIdentityMap: MutableMap<Identity, ArrayList<EntityId>> = mutableMapOf()

    fun getEntity(identity: Identity): EntityId? {
        return entityIdentityMap[identity]?.first()
    }

    fun getEntities(identity: Identity): List<EntityId>? {
        return entityIdentityMap[identity]
    }

    fun getRandomEntity(identity: Identity, rng: Random): EntityId? {
        val entities = entityIdentityMap[identity] ?: return null
        if (entities.size == 1)
            return entities.first()

        val randVal = rng.nextFloat()
        return entities[(entities.size * randVal).toInt()]
    }

    fun getAll(): Map<Identity, ArrayList<EntityId>> {
        return entityIdentityMap
    }

    fun add(entityIdentity: Pair<Identity, EntityName>): Tileset {
        if (entityIdentityMap[entityIdentity.first] == null)
            entityIdentityMap[entityIdentity.first] = ArrayList<EntityId>()
        entityIdentityMap[entityIdentity.first]!!.add(entityIdentity.second.id())
        return this
    }

    fun add(identity: Identity, entities: ArrayList<EntityName>): Tileset {
        if (entityIdentityMap[identity] == null)
            entityIdentityMap[identity] = ArrayList<EntityId>()
        entityIdentityMap[identity]!!.addAll(entities.map { it.id() })
        return this
    }

    fun add(entityIdentities: List<Pair<Identity, EntityName>>): Tileset {
        entityIdentities.forEach {
            if (entityIdentityMap[it.first] == null)
                entityIdentityMap[it.first] = ArrayList<EntityId>()
            entityIdentityMap[it.first]!!.add(it.second.id())
        }
        return this
    }

    fun add(tileset: Tileset): Tileset {
        tileset.getAll().toList().forEach {
            if (entityIdentityMap[it.first] == null)
                entityIdentityMap[it.first] = ArrayList<EntityId>()
            entityIdentityMap[it.first]!!.addAll(it.second)
        }
        return this
    }

    operator fun plusAssign(tileset: Tileset) {
        add(tileset)
    }

    constructor(entityIdentity: Pair<Identity, EntityName>): this() {
        entityIdentityMap[entityIdentity.first] = ArrayList<EntityId>().addInitial(entityIdentity.second.id())
    }

    constructor(identity: Identity, entities: ArrayList<EntityName>): this() {
        entityIdentityMap[identity] = entities.map { it.id() } as ArrayList<EntityId>
    }

    constructor(entityIdentities: List<Pair<Identity, EntityName>>): this() {
        entityIdentities.forEach {
            add(it)
//            entityIdentityMap[it.first] = ArrayList<EntityId>().addInitial(it.second.id())
        }
    }
}
