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
import com.neutrino.game.entities.items.attributes.HandheldEquipment
import com.neutrino.game.entities.items.attributes.HandheldEquipmentType
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.systems.requirements.Requirements
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
    }

    fun unequipItem(item: Entity, addToInventory: Boolean = true) {
        if (item.get(EquipmentItem::class)!!.isTwoHanded()) {
            equipmentMap[EquipmentType.LHAND] = null
            equipmentMap[EquipmentType.RHAND] = null
        } else
            equipmentMap[item.get(EquipmentItem::class)!!.getEquipmentType()] = null

        unsetItem(item, addToInventory)
    }

    private fun addItemAttributes(item: Item) {
        for (attribute in item.getItemAttributes()) {
            println("Adding attribute ${attribute::class}")
            if (entity.has(attribute::class))
                (entity.get(attribute::class)!! as AttributeOperations<Attribute>).plusEquals(attribute)
            else {
                println("Cloning")
                entity.addAttribute((attribute as Cloneable<Attribute>).clone())
            }
        }
        entity.call(OnItemEquipped::class, item)
        item.call(OnItemEquipped::class, entity)
    }

    private fun removeItemAttributes(item: Item) {
        for (attribute in item.getItemAttributes()) {
            (entity.get(attribute::class) as? AttributeOperations<Attribute>)?.minusEquals(attribute)
        }
        entity.call(OnItemUnequipped::class, item)
        item.call(OnItemUnequipped::class, entity)
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
        if (item.get(Requirements.Stats::class)?.check(Player) == false)
            return false
        if (item.get(Requirements.Custom::class)?.check(Player) == false)
            return false
        return true
    }

    fun getWeapon(): Entity? {
        if (getEquipped(EquipmentType.RHAND) != null)
            return getEquipped(EquipmentType.RHAND)
        if (getEquipped(EquipmentType.LHAND)?.get(HandheldEquipment::class)?.handheldType != HandheldEquipmentType.SHIELD)
            return getEquipped(EquipmentType.LHAND)
        return null
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