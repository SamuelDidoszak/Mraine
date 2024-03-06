package com.neutrino.game.entities.characters.attributes.util

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.OffensiveStats
import com.neutrino.game.entities.characters.callables.OnItemEquipped
import com.neutrino.game.entities.characters.callables.OnItemUnequipped
import com.neutrino.game.entities.items.attributes.EquipmentItem
import com.neutrino.game.entities.items.attributes.EquipmentType

sealed interface CharacterTag {
    fun onEntityAttached(entity: Entity)
    fun onEntityDetached(entity: Entity)

    /** ======================================================================================================================================================
    Strength based
     */
    class IncreaseMeleeDamage(
        var incrementPercent: Float
    ): CharacterTag {
        private val itemEquippedCallable = object : OnItemEquipped() {
            override fun call(entity: Entity, vararg data: Any?): Boolean {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true) {
                    entity.get(OffensiveStats::class)!!.damageMin += item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax += item.get(OffensiveStats::class)!!.damageMax * incrementPercent
                }
                return true
            }
        }
        private val itemUnequippedCallable = object : OnItemUnequipped() {
            override fun call(entity: Entity, vararg data: Any?): Boolean {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true) {
                    entity.get(OffensiveStats::class)!!.damageMin -= item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax -= item.get(OffensiveStats::class)!!.damageMax * incrementPercent
                }
                return true
            }
        }

        override fun onEntityAttached(entity: Entity) {
            entity.attach(itemEquippedCallable)
            entity.attach(itemUnequippedCallable)
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(itemEquippedCallable)
            entity.detach(itemUnequippedCallable)
        }
    }

    class IncreaseOnehandedDamage(
        var incrementPercent: Float
    ): CharacterTag {
        private val itemEquippedCallable = object : OnItemEquipped() {
            override fun call(entity: Entity, vararg data: Any?): Boolean {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true && item.get(EquipmentItem::class)?.type != EquipmentType.TWOHAND) {
                    entity.get(OffensiveStats::class)!!.damageMin += item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax += item.get(OffensiveStats::class)!!.damageMax * incrementPercent
                }
                return true
            }
        }
        private val itemUnequippedCallable = object : OnItemUnequipped() {
            override fun call(entity: Entity, vararg data: Any?): Boolean {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true && item.get(EquipmentItem::class)?.type != EquipmentType.TWOHAND) {
                    entity.get(OffensiveStats::class)!!.damageMin -= item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax -= item.get(OffensiveStats::class)!!.damageMax * incrementPercent
                }
                return true
            }
        }

        override fun onEntityAttached(entity: Entity) {
            entity.attach(itemEquippedCallable)
            entity.attach(itemUnequippedCallable)
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(itemEquippedCallable)
            entity.detach(itemUnequippedCallable)
        }
    }

    class IncreaseTwohandedDamage(
        var incrementPercent: Float
    ): CharacterTag {

        private val itemEquippedCallable = object : OnItemEquipped() {
            override fun call(entity: Entity, vararg data: Any?): Boolean {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true && item.get(EquipmentItem::class)?.type == EquipmentType.TWOHAND) {
                    entity.get(OffensiveStats::class)!!.damageMin += item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax += item.get(OffensiveStats::class)!!.damageMax * incrementPercent
                }
                return true
            }
        }
        private val itemUnequippedCallable = object : OnItemUnequipped() {
            override fun call(entity: Entity, vararg data: Any?): Boolean {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true && item.get(EquipmentItem::class)?.type == EquipmentType.TWOHAND) {
                    entity.get(OffensiveStats::class)!!.damageMin -= item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax -= item.get(OffensiveStats::class)!!.damageMax * incrementPercent
                }
                return true
            }
        }

        override fun onEntityAttached(entity: Entity) {
            entity.attach(itemEquippedCallable)
            entity.attach(itemUnequippedCallable)
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(itemEquippedCallable)
            entity.detach(itemUnequippedCallable)
        }
    }


}