package com.neutrino.game.entities.map_entities.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.map_entities.callables.InteractedCallable
import com.neutrino.game.entities.map_entities.util.Interactable

class PickUp: Attribute(), Interactable {
    override var requiredDistance: Int = 0
    override var turnCost: Double = 1.0
    override var isPrimary: Boolean = true

    override fun interact() {
        entity.call(InteractedCallable::class, this)
    }
}