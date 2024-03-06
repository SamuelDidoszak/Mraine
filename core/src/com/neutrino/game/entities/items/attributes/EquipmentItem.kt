package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.characters.attributes.Equipment
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.entities.util.Equality

data class EquipmentItem(val type: EquipmentType): Attribute(), Equality<EquipmentItem>, Cloneable<EquipmentItem> {

    override fun onEntityAttached() {
        if (entity.hasNot(ItemTier::class))
            entity.addAttribute(ItemTier(3))
        if (entity.hasNot(Amount::class))
            entity.addAttribute(Amount(maxStack = 1))
    }

    fun isMelee(): Boolean {
        if (type.isHandheld()) {
            return when (entity.get(HandheldEquipment::class)!!.handheldType) {
                HandheldEquipmentType.DAGGER,
                HandheldEquipmentType.SWORD,
                HandheldEquipmentType.AXE,
                HandheldEquipmentType.LANCE ->
                    true
                else ->
                    false
            }
        }
        return false
    }

    fun isRanged(): Boolean {
        if (type.isHandheld()) {
            return when (entity.get(HandheldEquipment::class)!!.handheldType) {
                HandheldEquipmentType.BOW,
                HandheldEquipmentType.CROSSBOW,
                HandheldEquipmentType.ARROW ->
                    true
                else ->
                    false
            }
        }
        return false
    }

    fun isMagicWeapon(): Boolean {
        if (type.isHandheld()) {
            return when (entity.get(HandheldEquipment::class)!!.handheldType) {
                HandheldEquipmentType.WAND,
                HandheldEquipmentType.STAFF,
                HandheldEquipmentType.PARCHMENT ->
                    true
                else ->
                    false
            }
        }
        return false
    }

    fun isTwoHanded(): Boolean {
        return type == EquipmentType.TWOHAND
    }

    fun getEquipmentType(): Equipment.EquipmentType {
        return when (type) {
            EquipmentType.HEAD -> Equipment.EquipmentType.HEAD
            EquipmentType.TORSO -> Equipment.EquipmentType.TORSO
            EquipmentType.LEGS -> Equipment.EquipmentType.LEGS
            EquipmentType.HANDS -> Equipment.EquipmentType.HANDS
            EquipmentType.FEET -> Equipment.EquipmentType.FEET
            EquipmentType.AMULET -> Equipment.EquipmentType.AMULET
            EquipmentType.LRING -> Equipment.EquipmentType.LRING
            EquipmentType.RRING -> Equipment.EquipmentType.RRING
            EquipmentType.BAG -> Equipment.EquipmentType.BAG
            EquipmentType.LHAND -> Equipment.EquipmentType.LHAND
            EquipmentType.RHAND -> Equipment.EquipmentType.RHAND
            else -> throw Exception("Items is of unsupported type")
        }
    }

    override fun isEqual(other: EquipmentItem): Boolean = type == other.type
    override fun clone(): EquipmentItem = EquipmentItem(type)
}

enum class EquipmentType {
    HEAD,
    TORSO,
    LEGS,
    FEET,
    HANDS,
    AMULET,
    LRING,
    RRING,
    LHAND,
    RHAND,
    TWOHAND,
    BAG;

    fun isHandheld(): Boolean {
        return  this == LHAND ||
                this == RHAND ||
                this == TWOHAND
    }
}