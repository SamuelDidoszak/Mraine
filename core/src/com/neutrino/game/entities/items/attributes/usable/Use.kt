package com.neutrino.game.entities.items.attributes.usable

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity

sealed class Use(
    val use: (entity: Entity) -> Unit
): Attribute() {

    fun use() = use.invoke(entity)
}