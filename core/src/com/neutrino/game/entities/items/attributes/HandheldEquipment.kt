package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.AttributeOperations

data class HandheldEquipment(
    val handheldType: HandheldEquipmentType,
    private val equipmentType: EquipmentType
): Attribute(), AttributeOperations<HandheldEquipment> {

    override fun onEntityAttached() {
        entity.addAttribute(EquipmentItem(equipmentType))
    }

    override fun plus(other: HandheldEquipment): HandheldEquipment = plusPrevious(other)
    override fun minus(other: HandheldEquipment): HandheldEquipment = minusPrevious(other)
    override fun isEqual(other: HandheldEquipment): Boolean = handheldType == other.handheldType
    override fun clone(): HandheldEquipment = copy()
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