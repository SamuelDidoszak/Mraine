package com.neutrino.game.map.generation.util

import com.neutrino.game.entities.Entities
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.util.EntityId
import com.neutrino.game.util.EntityName
import com.neutrino.game.util.hasIdentity
import com.neutrino.game.util.id
import kotlin.reflect.KClass

/**
 * @param not: true if rule has to be inverted. false if should be processed normally
 */
class NameOrIdentity(name: EntityName? = null, val identity: KClass<out Identity>? = null, val not: Boolean = false) {
    constructor(identity: KClass<out Identity>, not: Boolean = false) : this(null, identity, not)
    constructor(name: EntityName?, not: Boolean = false) : this(name, null, not)
    constructor(id: EntityId, not: Boolean = false) : this(Entities.getName(id), null, not)

    val id = name?.id()

    fun isSame(entity: Entity): Boolean {
        return (entity.id == id || (identity != null && entity hasIdentity identity)) == !not
    }

    fun isSame(name: EntityName): Boolean {
        return (id != null && Entities.getName(id) == name) == !not
    }

    fun isSame(id: EntityId): Boolean {
        return (this.id == id) == !not
    }

    fun getEntityName(): String? {
        if (id == null)
            return null
        return Entities.getName(id)
    }

    fun getIdentityName(): String? {
        if (identity == null)
            return null
        return identity.simpleName
    }

    override fun toString(): String {
        var params =
            if (id != null)
                "\"${getEntityName()}\""
            else
                "Identity." + getIdentityName() + "::class"
        if (not)
            params += ", true"
        return "NameOrIdentity($params)"
    }
}