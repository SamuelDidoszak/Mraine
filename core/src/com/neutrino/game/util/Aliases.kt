package com.neutrino.game.util

import com.neutrino.game.entities.Entities

typealias EntityId = Int
typealias EntityName = String

@JvmInline
value class EntityNameId(val id: EntityId) {
    constructor(name: EntityName) : this(Entities.getId(name))
}
