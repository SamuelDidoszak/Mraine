package com.neutrino.game.domain.model.items

import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.event.Requirement
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.utility.RandomizationTypes

abstract class EquipmentItem: Item(), ItemType.EQUIPMENT {
    override var amount: Int? = null
    /** Parses only ModifyStat, ModifyStatPercent and Event */
    abstract val modifierList: ArrayList<EventWrapper>
    override val itemTier: Int = 3
    open var requirements: Requirement = Requirement().add { true }

    fun statRandomization(energy: Float, variationSkew: Float = 0.5f, energyCanIncrease: Boolean = false, randomizationType: RandomizationTypes? = null) {
//        modifierList.sortBy { (it as ModifyStat).value as Float }
//        var energy = energy
//        while (energy.compareDelta(0f) == 1) {
//            val statToModify: ModifyStat = getWeighedStat(Constants.RandomGenerator.nextFloat()) // TODO randomize getting the stat with weight in an efficient way
//            // TODO Modify coerce percentage to be a custom percentage for each stat and to follow a certain curve that changes with game progression
//            // variation here is 0.2 -> 1 - 0.2 == 0.8 && 0.2 * 2 = 0.4
//            val variation = 0.2f
//            val valueModified = (statToModify.value as Float) * (1 - variation + Constants.RandomGenerator.nextFloat() * variation * 2)
//            var energyCost: Float = (valueModified - statToModify.value as Float) * statToModify.stat.statCost
//            energyCost = 1f // TODO implement thought out energy costs
//            if (!energyCanIncrease)
//                energyCost = abs(energyCost)
//            energy -= energyCost
//            (modifierList.find { (it as ModifyStat) == statToModify } as ModifyStat).value = valueModified
//        }
    }

    private fun getWeighedStat(value: Float): StatsEnum {
        // TODO weighing implementation
        return modifierList[value.coerceAtMost(modifierList.size.toFloat()).toInt()] as StatsEnum
    }

    fun isMelee(): Boolean {
        if (this is INHAND) {
            return when (this.handedItemType) {
                HandedItemType.DAGGER,
                HandedItemType.SWORD,
                HandedItemType.AXE,
                HandedItemType.LANCE ->
                    true
                else ->
                    false
            }
        }
        return true
    }

    fun isRanged(): Boolean {
        if (this is INHAND) {
            return when (this.handedItemType) {
                HandedItemType.BOW,
                HandedItemType.CROSSBOW,
                HandedItemType.ARROW ->
                    true
                else ->
                    false
            }
        }
        return true
    }

    fun isMagicWeapon(): Boolean {
        if (this is INHAND) {
            return when (this.handedItemType) {
                HandedItemType.WAND,
                HandedItemType.STAFF,
                HandedItemType.PARCHMENT ->
                    true
                else ->
                    false
            }
        }
        return true
    }

    fun getEquipmentType(): EquipmentType {
        return when (this as ItemType.EQUIPMENT) {
            is ItemType.EQUIPMENT.HEAD -> EquipmentType.HEAD
            is ItemType.EQUIPMENT.TORSO -> EquipmentType.TORSO
            is ItemType.EQUIPMENT.LEGS -> EquipmentType.LEGS
            is ItemType.EQUIPMENT.HANDS -> EquipmentType.HANDS
            is ItemType.EQUIPMENT.FEET -> EquipmentType.FEET
            is ItemType.EQUIPMENT.AMULET -> EquipmentType.AMULET
            is ItemType.EQUIPMENT.LRING -> EquipmentType.LRING
            is ItemType.EQUIPMENT.RRING -> EquipmentType.RRING
            is ItemType.EQUIPMENT.BAG -> EquipmentType.BAG
            is ItemType.EQUIPMENT.LHAND -> EquipmentType.LHAND
            is ItemType.EQUIPMENT.RHAND -> EquipmentType.RHAND
            is ItemType.EQUIPMENT.TWOHAND -> EquipmentType.RHAND
            else -> throw Exception("Items is of unsupported type")
        }
    }
}