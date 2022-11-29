package com.neutrino.game.domain.model.items

import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.HasInventory
import com.neutrino.game.domain.model.event.Data
import com.neutrino.game.domain.model.event.types.EventHeal
import com.neutrino.game.domain.model.event.types.EventModifyStat
import com.neutrino.game.domain.model.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.event.wrappers.EqItemStat
import com.neutrino.game.domain.model.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.items.utility.EqElement
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
            }
            is ItemType.EQUIPMENT.TWOHAND -> {
                val lHandItem = equipmentMap[EquipmentType.LHAND]
                if (lHandItem != null)
                    unsetItem(lHandItem)
                previousItem = equipmentMap[EquipmentType.RHAND]

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

    private fun unsetItem(item: EquipmentItem) {
        unsetItemModifiers(item)
        if (character is HasInventory) {
            character.inventory.itemList.add(EqElement(item as Item, Turn.turn))
        } else {
            // TODO drop item onto the ground or add to dropItemList
        }
    }

    private fun setItemModifiers(item: EquipmentItem) {
        for (modifier in item.modifierList) {
            when (modifier) {
                is EqItemStat -> {
                    modifier.event.attachData(character)
                    modifier.event.start()
                }
                is TimedEvent -> {
                    when (modifier.event) {
                        is EventHeal ->
                            (modifier.event as EventHeal).attachData(character)
                        is EventModifyStat ->
                            (modifier.event as EventModifyStat).attachData(character)
                    }
                    GlobalData.notifyObservers(GlobalDataType.EVENT, CharacterEvent(
                        character, modifier, Turn.turn
                    ))
                }
            }
        }
    }

    private fun unsetItemModifiers(item: EquipmentItem) {
        for (modifier in item.modifierList) {
            when (modifier) {
                is EqItemStat -> {
                    modifier.event.attachData(character)
                    modifier.event.start()
                }
                is TimedEvent -> {

                }
            }
        }
    }

    private fun checkRequirements(item: EquipmentItem): Boolean {
        if (item.requirements.data.containsKey("character"))
            (item.requirements.data["character"] as Data<Character>).setData(character)

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