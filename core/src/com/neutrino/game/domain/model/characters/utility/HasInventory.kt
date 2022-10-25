package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.domain.model.items.utility.Inventory

interface HasInventory {
    val inventory: Inventory
}