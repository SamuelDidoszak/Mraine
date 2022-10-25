package com.neutrino.game.domain.model.items

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.HasInventory
import com.neutrino.game.domain.model.characters.utility.ModifyStat
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.utility.EqElement
import com.neutrino.game.domain.model.turn.Event
import com.neutrino.game.domain.model.turn.Turn

class Equipment(val character: Character) {
    private val equipmentMap: MutableMap<EquipmentType, EquipmentItem?> = mutableMapOf(
        EquipmentType.HELMET to null,
        EquipmentType.ARMOR to null,
        EquipmentType.LEGGINGS to null,
        EquipmentType.BOOTS to null,
        EquipmentType.AMULET to null,
        EquipmentType.LRING to null,
        EquipmentType.RRING to null,
        EquipmentType.BAG to null,
        EquipmentType.LHAND to null,
        EquipmentType.RHAND to null
    )

    fun setItem(item: EquipmentItem) {
        var previousItem: EquipmentItem? = null
        when (item) {
            is ItemType.EQUIPMENT.HELMET -> {
                previousItem = equipmentMap[EquipmentType.HELMET]
                equipmentMap[EquipmentType.HELMET] = item
            }
            is ItemType.EQUIPMENT.ARMOR -> {
                previousItem = equipmentMap[EquipmentType.ARMOR]
                equipmentMap[EquipmentType.ARMOR] = item
            }
            is ItemType.EQUIPMENT.LEGGINGS -> {
                previousItem = equipmentMap[EquipmentType.LEGGINGS]
                equipmentMap[EquipmentType.LEGGINGS] = item
            }
            is ItemType.EQUIPMENT.BOOTS -> {
                previousItem = equipmentMap[EquipmentType.BOOTS]
                equipmentMap[EquipmentType.BOOTS] = item
            }
            is ItemType.EQUIPMENT.AMULET -> {
                previousItem = equipmentMap[EquipmentType.AMULET]
                equipmentMap[EquipmentType.AMULET] = item
            }
            is ItemType.EQUIPMENT.LRING -> {
                previousItem = equipmentMap[EquipmentType.LRING]
                equipmentMap[EquipmentType.LRING] = item
            }
            is ItemType.EQUIPMENT.RRING -> {
                previousItem = equipmentMap[EquipmentType.RRING]
                equipmentMap[EquipmentType.RRING] = item
            }
            is ItemType.EQUIPMENT.BAG -> {
                previousItem = equipmentMap[EquipmentType.BAG]
                equipmentMap[EquipmentType.BAG] = item
            }
            is ItemType.EQUIPMENT.INHAND -> {
                if (item is ItemType.EQUIPMENT.INHAND.ONEHANDED) {
                    previousItem = equipmentMap[EquipmentType.LHAND]
                    equipmentMap[EquipmentType.LHAND] = item
                }
                else if (item is ItemType.EQUIPMENT.INHAND.TWOHANDED) {
                    // TODO TWOHANDED unequip both hands
                    equipmentMap[EquipmentType.LHAND] = item
                    equipmentMap[EquipmentType.RHAND] = item
                }
            }
        }
        if (previousItem != null)
            unsetItemModifiers(previousItem)
        applyItemModifiers(item)

        if (character is HasInventory) {
            character.inventory.itemList.remove(
                character.inventory.itemList.find { it.item == item })
            if (previousItem != null)
                character.inventory.itemList.add(EqElement(previousItem as Item, Turn.turn))
        } else {
            // TODO drop item onto the ground or add to dropItemList
        }
    }

    private fun applyItemModifiers(item: EquipmentItem) {
        for (modifier in item.modifierList) {
            when (modifier) {
                // yucky, the ugliest thing ive ever wrote
                // adds a value and if percent = true multiplies it
                is ModifyStat -> {
                    when (modifier.stat) {
                        StatsEnum.HPMAX -> if (!modifier.percent) character.hpMax += modifier.value as Float else character.hpMax *= modifier.value as Float
                        StatsEnum.MPMAX -> if (!modifier.percent) character.mpMax += modifier.value as Float else character.mpMax *= modifier.value as Float
                        StatsEnum.STRENGTH -> if (!modifier.percent) character.strength += modifier.value as Float else character.strength *= modifier.value as Float
                        StatsEnum.DEXTERITY -> if (!modifier.percent) character.dexterity += modifier.value as Float else character.dexterity *= modifier.value as Float
                        StatsEnum.INTELLIGENCE -> if (!modifier.percent) character.intelligence += modifier.value as Float else character.intelligence *= modifier.value as Float
                        StatsEnum.LUCK -> if (!modifier.percent) character.luck += modifier.value as Float else character.luck *= modifier.value as Float
                        StatsEnum.DAMAGE -> if (!modifier.percent) character.damage += modifier.value as Float else character.damage *= modifier.value as Float
                        StatsEnum.DAMAGEVARIATION -> if (!modifier.percent) character.damageVariation += modifier.value as Float else character.damageVariation *= modifier.value as Float
                        StatsEnum.DEFENCE -> if (!modifier.percent) character.defence += modifier.value as Float else character.defence *= modifier.value as Float
                        StatsEnum.EVASION -> if (!modifier.percent) character.evasion += modifier.value as Float else character.evasion *= modifier.value as Float
                        StatsEnum.ACCURACY -> if (!modifier.percent) character.accuracy += modifier.value as Float else character.accuracy *= modifier.value as Float
                        StatsEnum.CRITICALCHANCE -> if (!modifier.percent) character.criticalChance += modifier.value as Float else character.criticalChance *= modifier.value as Float
                        StatsEnum.CRITICALDAMAGE -> if (!modifier.percent) character.criticalDamage += modifier.value as Float else character.criticalDamage *= modifier.value as Float
                        StatsEnum.ATTACKSPEED -> if (!modifier.percent) character.attackSpeed += modifier.value as Double else character.attackSpeed *= modifier.value as Double
                        StatsEnum.MOVEMENTSPEED -> if (!modifier.percent) character.movementSpeed += modifier.value as Double else character.movementSpeed *= modifier.value as Double
                        StatsEnum.RANGE -> if (!modifier.percent) character.range += modifier.value as Int else character.range *= modifier.value as Int
                        StatsEnum.RANGETYPE -> if (!modifier.percent) character.rangeType = modifier.value as RangeType else character.rangeType = modifier.value as RangeType
                        StatsEnum.FIREDAMAGE -> if (!modifier.percent) character.fireDamage += modifier.value as Float else character.fireDamage *= modifier.value as Float
                        StatsEnum.WATERDAMAGE -> if (!modifier.percent) character.waterDamage += modifier.value as Float else character.waterDamage *= modifier.value as Float
                        StatsEnum.EARTHDAMAGE -> if (!modifier.percent) character.earthDamage += modifier.value as Float else character.earthDamage *= modifier.value as Float
                        StatsEnum.AIRDAMAGE -> if (!modifier.percent) character.airDamage += modifier.value as Float else character.airDamage *= modifier.value as Float
                        StatsEnum.POISONDAMAGE -> if (!modifier.percent) character.poisonDamage += modifier.value as Float else character.poisonDamage *= modifier.value as Float
                        StatsEnum.FIREDEFENCE -> if (!modifier.percent) character.fireDefence += modifier.value as Float else character.fireDefence *= modifier.value as Float
                        StatsEnum.WATERDEFENCE -> if (!modifier.percent) character.waterDefence += modifier.value as Float else character.waterDefence *= modifier.value as Float
                        StatsEnum.EARTHDEFENCE -> if (!modifier.percent) character.earthDefence += modifier.value as Float else character.earthDefence *= modifier.value as Float
                        StatsEnum.AIRDEFENCE -> if (!modifier.percent) character.airDefence += modifier.value as Float else character.airDefence *= modifier.value as Float
                        StatsEnum.POISONDEFENCE -> if (!modifier.percent) character.poisonDefence += modifier.value as Float else character.poisonDefence *= modifier.value as Float
                    }
                }
                is Event -> {
                    modifier.turn = Turn.turn
                    modifier.repeats = Int.MAX_VALUE
                    Turn.eventArray.add(modifier)
                }
            }
        }
    }

    fun unsetItemModifiers(item: EquipmentItem) {
        for (modifier in item.modifierList) {
            when (modifier) {
                // yucky, the ugliest thing ive ever wrote
                // adds a value and if percent = true multiplies it
                is ModifyStat -> {
                    when (modifier.stat) {
                        StatsEnum.HPMAX -> if (!modifier.percent) character.hpMax -= modifier.value as Float else character.hpMax /= modifier.value as Float
                        StatsEnum.MPMAX -> if (!modifier.percent) character.mpMax -= modifier.value as Float else character.mpMax /= modifier.value as Float
                        StatsEnum.STRENGTH -> if (!modifier.percent) character.strength -= modifier.value as Float else character.strength /= modifier.value as Float
                        StatsEnum.DEXTERITY -> if (!modifier.percent) character.dexterity -= modifier.value as Float else character.dexterity /= modifier.value as Float
                        StatsEnum.INTELLIGENCE -> if (!modifier.percent) character.intelligence -= modifier.value as Float else character.intelligence /= modifier.value as Float
                        StatsEnum.LUCK -> if (!modifier.percent) character.luck -= modifier.value as Float else character.luck /= modifier.value as Float
                        StatsEnum.DAMAGE -> if (!modifier.percent) character.damage -= modifier.value as Float else character.damage /= modifier.value as Float
                        StatsEnum.DAMAGEVARIATION -> if (!modifier.percent) character.damageVariation -= modifier.value as Float else character.damageVariation /= modifier.value as Float
                        StatsEnum.DEFENCE -> if (!modifier.percent) character.defence -= modifier.value as Float else character.defence /= modifier.value as Float
                        StatsEnum.EVASION -> if (!modifier.percent) character.evasion -= modifier.value as Float else character.evasion /= modifier.value as Float
                        StatsEnum.ACCURACY -> if (!modifier.percent) character.accuracy -= modifier.value as Float else character.accuracy /= modifier.value as Float
                        StatsEnum.CRITICALCHANCE -> if (!modifier.percent) character.criticalChance -= modifier.value as Float else character.criticalChance /= modifier.value as Float
                        StatsEnum.CRITICALDAMAGE -> if (!modifier.percent) character.criticalDamage -= modifier.value as Float else character.criticalDamage /= modifier.value as Float
                        StatsEnum.ATTACKSPEED -> if (!modifier.percent) character.attackSpeed -= modifier.value as Double else character.attackSpeed /= modifier.value as Double
                        StatsEnum.MOVEMENTSPEED -> if (!modifier.percent) character.movementSpeed -= modifier.value as Double else character.movementSpeed /= modifier.value as Double
                        StatsEnum.RANGE -> if (!modifier.percent) character.range -= modifier.value as Int else character.range /= modifier.value as Int
                        // TODO DUALWIELD if there is dual wield change rangetype to the second weapon rangetype
                        StatsEnum.RANGETYPE -> if (!modifier.percent) character.rangeType = modifier.value as RangeType else character.rangeType = modifier.value as RangeType
                        StatsEnum.FIREDAMAGE -> if (!modifier.percent) character.fireDamage -= modifier.value as Float else character.fireDamage /= modifier.value as Float
                        StatsEnum.WATERDAMAGE -> if (!modifier.percent) character.waterDamage -= modifier.value as Float else character.waterDamage /= modifier.value as Float
                        StatsEnum.EARTHDAMAGE -> if (!modifier.percent) character.earthDamage -= modifier.value as Float else character.earthDamage /= modifier.value as Float
                        StatsEnum.AIRDAMAGE -> if (!modifier.percent) character.airDamage -= modifier.value as Float else character.airDamage /= modifier.value as Float
                        StatsEnum.POISONDAMAGE -> if (!modifier.percent) character.poisonDamage -= modifier.value as Float else character.poisonDamage /= modifier.value as Float
                        StatsEnum.FIREDEFENCE -> if (!modifier.percent) character.fireDefence -= modifier.value as Float else character.fireDefence /= modifier.value as Float
                        StatsEnum.WATERDEFENCE -> if (!modifier.percent) character.waterDefence -= modifier.value as Float else character.waterDefence /= modifier.value as Float
                        StatsEnum.EARTHDEFENCE -> if (!modifier.percent) character.earthDefence -= modifier.value as Float else character.earthDefence /= modifier.value as Float
                        StatsEnum.AIRDEFENCE -> if (!modifier.percent) character.airDefence -= modifier.value as Float else character.airDefence /= modifier.value as Float
                        StatsEnum.POISONDEFENCE -> if (!modifier.percent) character.poisonDefence -= modifier.value as Float else character.poisonDefence /= modifier.value as Float
                    }
                }
                is Event -> {
                    Turn.eventArray.remove(Turn.eventArray.find { it == modifier })
                }
            }
        }
    }
} enum class EquipmentType {
    HELMET,
    ARMOR,
    LEGGINGS,
    BOOTS,
    AMULET,
    LRING,
    RRING,
    BAG,
    LHAND,
    RHAND
}