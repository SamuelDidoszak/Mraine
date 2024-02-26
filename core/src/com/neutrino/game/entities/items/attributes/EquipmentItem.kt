package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute

class EquipmentItem(val type: EquipmentType): Attribute() {

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