package com.neutrino.game.entities.items.attributes.usable

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position

class UseOnPosition(
    val use: (entity: Entity, position: Position) -> Unit
): Attribute() {

    fun use(position: Position) = use.invoke(entity, position)
}