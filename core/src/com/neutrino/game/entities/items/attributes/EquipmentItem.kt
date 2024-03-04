package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.AttributeOperations

data class EquipmentItem(val type: EquipmentType): Attribute(), AttributeOperations<EquipmentItem> {

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

    override fun plus(other: EquipmentItem): EquipmentItem = plusPrevious(other)
    override fun minus(other: EquipmentItem): EquipmentItem = minusPrevious(other)
    override fun clone(): EquipmentItem = copy()
    override fun isEqual(other: EquipmentItem): Boolean = type == other.type
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