package com.neutrino.game.domain.model.items.equipment.utility

data class Equipment(
    val itemList: MutableList<EqElement> = ArrayList(),
    val name: String? = null,

    )