package com.neutrino.game.domain.model.items

import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.HasInventory
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.utility.EqElement
import com.neutrino.game.domain.model.systems.CharacterTag
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.turn.Turn
import java.util.*

class Equipment(val character: Character) {
    private val equipmentMap: EnumMap<EquipmentType, EquipmentItem?> = EnumMap(EquipmentType::class.java)

    fun getEquipped(type: EquipmentType): EquipmentItem? {
        return equipmentMap[type]
    }

    /** Unequips an item from a certain slot
     * @param type Equipment slot from which an item should be unequipped
     * */
    fun setItem(type: EquipmentType): EquipmentType? {
        val equippedItem = equipmentMap[type] ?: return null
        unsetItem(equippedItem)
        return type
    }

    /** Equips an item
     * @return EquipmentType of the item for updates
     * @null when an item was taken off */
    fun setItem(item: EquipmentItem): EquipmentType? {
        var previousItem: EquipmentItem? = null
        var equipmentType: EquipmentType? = null

        if (!checkRequirements(item))
            return null

        when (item) {
            is ItemType.EQUIPMENT.HEAD -> {
                previousItem = equipmentMap[EquipmentType.HEAD]
                equipmentMap[EquipmentType.HEAD] = item
                equipmentType = EquipmentType.HEAD
            }
            is ItemType.EQUIPMENT.TORSO -> {
                previousItem = equipmentMap[EquipmentType.TORSO]
                equipmentMap[EquipmentType.TORSO] = item
                equipmentType = EquipmentType.TORSO
            }
            is ItemType.EQUIPMENT.LEGS -> {
                previousItem = equipmentMap[EquipmentType.LEGS]
                equipmentMap[EquipmentType.LEGS] = item
                equipmentType = EquipmentType.LEGS
            }
            is ItemType.EQUIPMENT.FEET -> {
                previousItem = equipmentMap[EquipmentType.FEET]
                equipmentMap[EquipmentType.FEET] = item
                equipmentType = EquipmentType.FEET
            }
            is ItemType.EQUIPMENT.HANDS -> {
                previousItem = equipmentMap[EquipmentType.HANDS]
                equipmentMap[EquipmentType.HANDS] = item
                equipmentType = EquipmentType.HANDS
            }
            is ItemType.EQUIPMENT.AMULET -> {
                previousItem = equipmentMap[EquipmentType.AMULET]
                equipmentMap[EquipmentType.AMULET] = item
                equipmentType = EquipmentType.AMULET
            }
            is ItemType.EQUIPMENT.LRING -> {
                previousItem = equipmentMap[EquipmentType.LRING]
                equipmentMap[EquipmentType.LRING] = item
                equipmentType = EquipmentType.LRING
            }
            is ItemType.EQUIPMENT.RRING -> {
                previousItem = equipmentMap[EquipmentType.RRING]
                equipmentMap[EquipmentType.RRING] = item
                equipmentType = EquipmentType.RRING
            }
            is ItemType.EQUIPMENT.BAG -> {
                previousItem = equipmentMap[EquipmentType.BAG]
                equipmentMap[EquipmentType.BAG] = item
                equipmentType = EquipmentType.BAG
            }
            is ItemType.EQUIPMENT.LHAND -> {
                previousItem = equipmentMap[EquipmentType.LHAND]
                equipmentMap[EquipmentType.LHAND] = item
                equipmentType = EquipmentType.LHAND
            }
            is ItemType.EQUIPMENT.RHAND -> {
                previousItem = equipmentMap[EquipmentType.RHAND]
                equipmentMap[EquipmentType.RHAND] = item
                equipmentType = EquipmentType.RHAND
                character.primaryAttack = item.attack


                if (item.isMelee() && character.tags.contains(CharacterTag.IncreaseOnehandedDamage::class)) {
                    item.modifierList.forEach {
                        if (it.event is EventModifyStat && (it.event as EventModifyStat).stat == StatsEnum.DAMAGE) {
                            val value = (it.event as EventModifyStat).value as Float * (character.getTag(CharacterTag.IncreaseOnehandedDamage::class))!!.incrementPercent
                            (it.event as EventModifyStat).value = value
                            return@forEach
                        }
                    }
                }
            }
            is ItemType.EQUIPMENT.TWOHAND -> {
                val lHandItem = equipmentMap[EquipmentType.LHAND]
                if (lHandItem != null)
                    unsetItem(lHandItem)
                previousItem = equipmentMap[EquipmentType.RHAND]

                if (item.isMelee() && character.tags.contains(CharacterTag.IncreaseTwohandedDamage::class)) {
                    item.modifierList.forEach {
                        if (it.event is EventModifyStat && (it.event as EventModifyStat).stat == StatsEnum.DAMAGE) {
                            val value = (it.event as EventModifyStat).value as Float * (character.getTag(CharacterTag.IncreaseTwohandedDamage::class))!!.incrementPercent
                            (it.event as EventModifyStat).value = value
                            return@forEach
                        }
                    }
                }

                equipmentMap[EquipmentType.LHAND] = item
                equipmentMap[EquipmentType.RHAND] = item
                equipmentType = EquipmentType.RHAND
            }
        }
        if (character is HasInventory)
            character.inventory.itemList.remove(
                character.inventory.itemList.find { it.item == item })

        if (previousItem != null)
            unsetItem(previousItem)
        setItemModifiers(item)

        return equipmentType
    }

    fun unsetItem(item: EquipmentItem, addToInventory: Boolean = true) {
        unsetItemModifiers(item)

        if (item.isMelee() && character.tags.contains(CharacterTag.IncreaseOnehandedDamage::class)) {
            item.modifierList.forEach {
                if (it.event is EventModifyStat && (it.event as EventModifyStat).stat == StatsEnum.DAMAGE) {
                    val value = (it.event as EventModifyStat).value as Float / (character.getTag(CharacterTag.IncreaseOnehandedDamage::class))!!.incrementPercent
                    (it.event as EventModifyStat).value = value
                    return@forEach
                }
            }
        }
        if (item.isMelee() && character.tags.contains(CharacterTag.IncreaseTwohandedDamage::class)) {
            item.modifierList.forEach {
                if (it.event is EventModifyStat && (it.event as EventModifyStat).stat == StatsEnum.DAMAGE) {
                    val value = (it.event as EventModifyStat).value as Float / (character.getTag(CharacterTag.IncreaseTwohandedDamage::class))!!.incrementPercent
                    (it.event as EventModifyStat).value = value
                    return@forEach
                }
            }
        }

        if (item is INHAND)
            character.primaryAttack = character.basicAttack

        equipmentMap[item.getEquipmentType()] = null

        if (!addToInventory)
            return

        if (character is HasInventory) {
            character.inventory.itemList.add(EqElement(item as Item, Turn.turn))
            // required to add it to hud bar
            if (character is Player)
                GlobalData.notifyObservers(GlobalDataType.PICKUP, item)
        } else {
            // TODO drop item onto the ground or add to dropItemList
        }
    }

    private fun setItemModifiers(item: EquipmentItem) {
        for (modifier in item.modifierList) {
            if (modifier.event.has("character"))
                modifier.event.set("character", character)
            when (modifier) {
                is OnOffEvent -> {
                    modifier.event.start()
                }
                is TimedEvent -> {
                    GlobalData.notifyObservers(GlobalDataType.EVENT, CharacterEvent(
                        character, modifier, Turn.turn
                    ))
                }
            }
        }
        if (item is INHAND)
            character.primaryAttack = (item as INHAND).attack
    }

    private fun unsetItemModifiers(item: EquipmentItem) {
        for (modifier in item.modifierList) {
            if (modifier.event.has("character"))
                modifier.event.set("character", character)
            when (modifier) {
                is OnOffEvent -> {
                    modifier.event.stop()
                }
                is TimedEvent -> {

                }
            }
        }
    }

    private fun checkRequirements(item: EquipmentItem): Boolean {
        if (item.requirements.has("character"))
            item.requirements.set("character", character)

        return item.requirements.checkAll()
    }
}

enum class EquipmentType {
    HEAD,
    TORSO,
    LEGS,
    FEET,
    AMULET,
    LRING,
    RRING,
    BAG,
    HANDS,
    LHAND,
    RHAND,
    MONEY
}