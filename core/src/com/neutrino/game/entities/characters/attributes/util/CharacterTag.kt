package com.neutrino.game.entities.characters.attributes.util

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.callables.OnItemEquipped
import com.neutrino.game.entities.characters.callables.OnItemUnequipped
import com.neutrino.game.entities.items.attributes.EquipmentItem
import com.neutrino.game.entities.items.attributes.EquipmentType
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.attack.callables.LifestealCallable
import com.neutrino.game.entities.systems.attack.callables.StatsChangedCallable
import com.neutrino.game.entities.systems.attack.util.StatsEnum
import com.neutrino.game.util.equalsDelta

sealed interface CharacterTag {
    fun onEntityAttached(entity: Entity) {}
    fun onEntityDetached(entity: Entity) {}

    /** ======================================================================================================================================================
    Strength based
     */
    class IncreaseMeleeDamage(
        var incrementPercent: Float
    ): CharacterTag {
        private val itemEquippedCallable = object : OnItemEquipped() {
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true) {
                    entity.get(OffensiveStats::class)!!.damageMin += item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax += item.get(OffensiveStats::class)!!.damageMax * incrementPercent
            } }
        }
        private val itemUnequippedCallable = object : OnItemUnequipped() {
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true) {
                    entity.get(OffensiveStats::class)!!.damageMin -= item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax -= item.get(OffensiveStats::class)!!.damageMax * incrementPercent
            } }
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
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true && item.get(EquipmentItem::class)?.type != EquipmentType.TWOHAND) {
                    entity.get(OffensiveStats::class)!!.damageMin += item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax += item.get(OffensiveStats::class)!!.damageMax * incrementPercent
            } }
        }
        private val itemUnequippedCallable = object : OnItemUnequipped() {
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true && item.get(EquipmentItem::class)?.type != EquipmentType.TWOHAND) {
                    entity.get(OffensiveStats::class)!!.damageMin -= item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax -= item.get(OffensiveStats::class)!!.damageMax * incrementPercent
            } }
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
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true && item.get(EquipmentItem::class)?.type == EquipmentType.TWOHAND) {
                    entity.get(OffensiveStats::class)!!.damageMin += item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax += item.get(OffensiveStats::class)!!.damageMax * incrementPercent
            } }
        }
        private val itemUnequippedCallable = object : OnItemUnequipped() {
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                if (item.get(EquipmentItem::class)?.isMelee() == true && item.get(EquipmentItem::class)?.type == EquipmentType.TWOHAND) {
                    entity.get(OffensiveStats::class)!!.damageMin -= item.get(OffensiveStats::class)!!.damageMin * incrementPercent
                    entity.get(OffensiveStats::class)!!.damageMax -= item.get(OffensiveStats::class)!!.damageMax * incrementPercent
            } }
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

    class Berserk(
        val hpThresholdPercent: Float,
        val incrementPercent: Float
    ): CharacterTag {

        private var baseDamageMin = 0f
        private var baseDamageMax = 0f

        private fun Float.increase(entity: Entity): Float {
            val defensiveStats = entity.get(DefensiveStats::class)!!
            return this * (1f + (incrementPercent - 1) * (1 - (defensiveStats.hp / (defensiveStats.hpMax * hpThresholdPercent))))
        }

        private fun setBaseDamage(entity: Entity) {
            val damageMin = entity.get(OffensiveStats::class)!!.damageMin
            val damageMax = entity.get(OffensiveStats::class)!!.damageMax

            if (!baseDamageMin.increase(entity).equalsDelta(damageMin))
                baseDamageMin = damageMin
            if (!baseDamageMax.increase(entity).equalsDelta(damageMax))
                baseDamageMax = damageMax
        }

        private val berserkCallable = object : StatsChangedCallable() {
            override fun call(entity: Entity, vararg data: Any?) {
                if (data[0] == StatsEnum.DAMAGE)
                    setBaseDamage(entity)
                if (data[0] != StatsEnum.HP)
                    return
                val defensiveStats = entity.get(DefensiveStats::class)!!
                val offensiveStats = entity.get(OffensiveStats::class)!!
                if (defensiveStats.hp / defensiveStats.hpMax > hpThresholdPercent)
                    return

                offensiveStats.damageMin = offensiveStats.damageMin.increase(entity)
                offensiveStats.damageMax = offensiveStats.damageMax.increase(entity)
            }
        }

        override fun onEntityAttached(entity: Entity) {
            entity.attach(berserkCallable)
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(berserkCallable)
            val offensiveStats = entity.get(OffensiveStats::class)!!
            offensiveStats.damageMin = baseDamageMin
            offensiveStats.damageMax = baseDamageMax
        }
    }

    class Lifesteal(
        var power: Float
    ): CharacterTag {
        private val lifestealCallable = LifestealCallable()

        override fun onEntityAttached(entity: Entity) {
            entity.attach(lifestealCallable)
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(lifestealCallable)
        }
    }

    /** ======================================================================================================================================================
                                                                        Others
     */

    class IncreaseStealthDamage(
        var incrementPercent: Float
    ): CharacterTag

    class ReduceCooldown(
        var reducePercent: Float
    ): CharacterTag

}