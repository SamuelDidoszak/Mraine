package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute

class HandheldEquipment(
    val handheldType: HandheldEquipmentType,
    private val equipmentType: EquipmentType
): Attribute(){

    override fun onEntityAttached() {
        entity.addAttribute(EquipmentItem(equipmentType))
    }
}

enum class HandheldEquipmentType {
    SHIELD,

    // Weapon types
    // melee
    DAGGER,
    SWORD,
    AXE,
    LANCE,

    // ranged
    BOW,
    CROSSBOW,
    ARROW,

    // magic
    WAND,
    STAFF,
    PARCHMENT
}