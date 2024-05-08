package com.neutrino.game.entities.map_entities.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.Inventory
import com.neutrino.game.entities.characters.callables.attack.DropItemsCallable
import com.neutrino.game.entities.shared.attributes.Identity

/**
 * @param destructable if null, entity will not be destructable
 */
class Container(
    private val destructable: Destructable?,
    private val size: Int = 10,
    private val initItems: List<Entity>? = null): Attribute() {
    override fun onEntityAttached() {
        entity.addAttribute(Inventory(size, initItems))
        entity.addAttribute(Identity.Container())
        if (destructable != null) {
            entity.addAttribute(destructable)
            entity.attach(DropItemsCallable())
        }
    }
}