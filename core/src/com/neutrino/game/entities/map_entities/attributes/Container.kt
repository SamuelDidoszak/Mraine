package com.neutrino.game.entities.map_entities.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.characters.attributes.Inventory
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.entities.systems.attack.callables.DropItemsCallable
import com.neutrino.game.map.generation.util.ItemAdder

/**
 * @param destructable if null, entity will not be destructable
 */
class Container(
    private val destructable: Destructable?,
    private val itemAdder: ItemAdder,
    private val size: Int = 10): Attribute() {
    override fun onEntityAttached() {
        entity.addAttribute(Inventory(size))
        entity.addAttribute(itemAdder)
        entity.addAttribute(Identity.Container())
        if (destructable != null) {
            entity.addAttribute(destructable)
            entity.attach(DropItemsCallable())
        }
    }
}