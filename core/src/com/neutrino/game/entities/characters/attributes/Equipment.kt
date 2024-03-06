package com.neutrino.game.entities.characters.attributes

import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.callables.OnItemEquipped
import com.neutrino.game.entities.characters.callables.OnItemUnequipped
import com.neutrino.game.entities.items.Item
import com.neutrino.game.entities.items.attributes.EquipmentItem
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.util.AttributeOperations
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.map.chunk.ChunkManager
import java.util.*

class Equipment: Attribute() {
    private val equipmentMap: EnumMap<EquipmentType, Entity?> = EnumMap(EquipmentType::class.java)

    fun getEquipped(type: EquipmentType): Entity? {
        return equipmentMap[type]
    }

    fun equipItem(item: Entity) {
        if (!checkRequirements(item))
            return

        if (item.get(EquipmentItem::class)!!.isTwoHanded()) {
            equipmentMap[EquipmentType.LHAND]?.let { unsetItem(it) }
            equipmentMap[EquipmentType.RHAND]?.let { unsetItem(it) }

            equipmentMap[EquipmentType.LHAND] = item
            equipmentMap[EquipmentType.RHAND] = item
        } else {
            val equipmentType = item.get(EquipmentItem::class)!!.getEquipmentType()
            equipmentMap[equipmentType]?.let { unsetItem(it) }
            equipmentMap[equipmentType] = item
        }

        addItemAttributes(item as Item)
        entity.call(OnItemEquipped::class)
    }

    fun unequipItem(item: Entity, addToInventory: Boolean = true) {
        if (item.get(EquipmentItem::class)!!.isTwoHanded()) {
            equipmentMap[EquipmentType.LHAND] = null
            equipmentMap[EquipmentType.RHAND] = null
        } else
            equipmentMap[item.get(EquipmentItem::class)!!.getEquipmentType()] = null

        unsetItem(item, addToInventory)
        entity.call(OnItemUnequipped::class)
    }

    private fun addItemAttributes(item: Item) {
        for (attribute in item.getItemAttributes()) {
            if (entity.has(attribute::class))
                (entity.get(attribute::class)!! as AttributeOperations<Attribute>).plusEquals(attribute)
            else
                entity.addAttribute((attribute as Cloneable<Attribute>).clone())
        }
    }

    private fun removeItemAttributes(item: Item) {
        for (attribute in item.getItemAttributes()) {
            (entity.get(attribute::class) as? AttributeOperations<Attribute>)?.minusEquals(attribute)
        }
    }

    private fun unsetItem(item: Entity, addToInventory: Boolean = true) {
        removeItemAttributes(item as Item)

        if (addToInventory) {
            entity.get(Inventory::class)?.add(item)
            if (entity == Player)
                GlobalData.notifyObservers(GlobalDataType.PICKUP, item)
        } else
            ChunkManager.getEntitiesAt(entity.get(Position::class)!!).add(item)
    }

    private fun checkRequirements(item: Entity): Boolean {
        return true
//        if (item.requirements.has("character"))
//            item.requirements.set("character", character)
//
//        return item.requirements.checkAll()
    }

    enum class EquipmentType {
        HEAD,
        TORSO,
        HANDS,
        LEGS,
        FEET,
        AMULET,
        LRING,
        RRING,
        LHAND,
        RHAND,
        BAG,
        MONEY
    }
}