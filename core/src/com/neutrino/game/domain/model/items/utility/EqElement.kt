package com.neutrino.game.domain.model.items.utility

import com.neutrino.game.domain.model.items.Item

data class EqElement(
    val item: Item,
    /** Turn at which the item was added to the inventory */
    val dateAdded: Double,
    var customPosition: Int? = 0
)