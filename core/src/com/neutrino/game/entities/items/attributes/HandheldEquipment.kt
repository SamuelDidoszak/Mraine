package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.entities.util.Equality

data class HandheldEquipment(
    val handheldType: HandheldEquipmentType,
    private val equipmentType: EquipmentType
): Attribute(), Equality<HandheldEquipment>, Cloneable<HandheldEquipment> {

    override fun onEntityAttached() {
        entity.addAttribute(EquipmentItem(equipmentType))
    }

    override fun isEqual(other: HandheldEquipment): Boolean = handheldType == other.handheldType && equipmentType == other.equipmentType
    override fun clone(): HandheldEquipment = HandheldEquipment(handheldType, equipmentType)
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