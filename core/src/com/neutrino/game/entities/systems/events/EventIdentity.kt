package com.neutrino.game.entities.systems.events

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position

data class EventIdentity(
    val entity: Entity? = null,
    val position: Position? = null
)
