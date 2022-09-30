package com.neutrino.game.domain.model.items.equipment.utility

data class Inventory(
    val itemList: MutableList<EqElement> = ArrayList(),
    val name: String? = null
)