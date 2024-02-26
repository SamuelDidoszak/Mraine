package com.neutrino.game.entities.characters.attributes.util

import com.neutrino.game.entities.Entity

data class InventoryElement(
    val item: Entity,
    /** Turn at which the item was added to the inventory */
    val dateAdded: Double,
    /** Player chosen position */
    var customPosition: Int
)
