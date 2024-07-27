package com.neutrino.game.entities.characters.attributes.util

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.Equipment
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
        private fun setStats(entity: Entity, item: Entity, add: Boolean) {
            if (item.get(EquipmentItem::class)?.isMelee() == true) {
                val modifier = if (add) 1f else -1f
                entity.get(OffensiveStats::class)!!.damageMin += item.get(OffensiveStats::class)!!.damageMin * incrementPercent * modifier
                entity.get(OffensiveStats::class)!!.damageMax += item.get(OffensiveStats::class)!!.damageMax * incrementPercent * modifier
            }
        }

        private val itemEquippedCallable = object : OnItemEquipped() {
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                setStats(entity, item, true)
            }
        }
        private val itemUnequippedCallable = object : OnItemUnequipped() {
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                setStats(entity, item, false)
            }
        }

        override fun onEntityAttached(entity: Entity) {
            entity.attach(itemEquippedCallable)
            entity.attach(itemUnequippedCallable)
            val weapon = entity.get(Equipment::class)?.getWeapon()
            if (weapon != null)
                setStats(entity, weapon, true)
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(itemEquippedCallable)
            entity.detach(itemUnequippedCallable)
            val weapon = entity.get(Equipment::class)?.getWeapon()
            if (weapon != null)
                setStats(entity, weapon, false)
        }
    }

    class IncreaseOnehandedDamage(
        var incrementPercent: Float
    ): CharacterTag {
        private fun setStats(entity: Entity, item: Entity, add: Boolean) {
            if (item.get(EquipmentItem::class)?.isMelee() == true && item.get(EquipmentItem::class)?.type != EquipmentType.TWOHAND) {
                val modifier = if (add) 1f else -1f
                entity.get(OffensiveStats::class)!!.damageMin += item.get(OffensiveStats::class)!!.damageMin * incrementPercent * modifier
                entity.get(OffensiveStats::class)!!.damageMax += item.get(OffensiveStats::class)!!.damageMax * incrementPercent * modifier
            }
        }

        private val itemEquippedCallable = object : OnItemEquipped() {
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                setStats(entity, item, true)
            }
        }
        private val itemUnequippedCallable = object : OnItemUnequipped() {
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                setStats(entity, item, false)
            }
        }

        override fun onEntityAttached(entity: Entity) {
            entity.attach(itemEquippedCallable)
            entity.attach(itemUnequippedCallable)
            val weapon = entity.get(Equipment::class)?.getWeapon()
            if (weapon != null)
                setStats(entity, weapon, true)
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(itemEquippedCallable)
            entity.detach(itemUnequippedCallable)
            val weapon = entity.get(Equipment::class)?.getWeapon()
            if (weapon != null)
                setStats(entity, weapon, false)
        }
    }

    class IncreaseTwohandedDamage(
        var incrementPercent: Float
    ): CharacterTag {
        private fun setStats(entity: Entity, item: Entity, add: Boolean) {
            if (item.get(EquipmentItem::class)?.isMelee() == true && item.get(EquipmentItem::class)?.type == EquipmentType.TWOHAND) {
                val modifier = if (add) 1 else -1
                entity.get(OffensiveStats::class)!!.damageMin += item.get(OffensiveStats::class)!!.damageMin * incrementPercent * modifier
                entity.get(OffensiveStats::class)!!.damageMax += item.get(OffensiveStats::class)!!.damageMax * incrementPercent * modifier
            }
        }

        private val itemEquippedCallable = object : OnItemEquipped() {
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                setStats(entity, item, true)
            }
        }
        private val itemUnequippedCallable = object : OnItemUnequipped() {
            override fun call(entity: Entity, vararg data: Any?) {
                val item = data[0] as Entity
                setStats(entity, item, false)
            }
        }

        override fun onEntityAttached(entity: Entity) {
            entity.attach(itemEquippedCallable)
            entity.attach(itemUnequippedCallable)
            val weapon = entity.get(Equipment::class)?.getWeapon()
            if (weapon != null)
                setStats(entity, weapon, true)
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(itemEquippedCallable)
            entity.detach(itemUnequippedCallable)
            val weapon = entity.get(Equipment::class)?.getWeapon()
            if (weapon != null)
                setStats(entity, weapon, false)
        }
    }

    class Berserk(
        val hpThresholdPercent: Float,
        val incrementPercent: Float
    ): CharacterTag {

        private var baseDamageMin = 0f
        private var baseDamageMax = 0f
        private var lastDamageMin = 0f
        private var lastDamageMax = 0f
        private lateinit var offensiveStats: OffensiveStats
        private lateinit var defensiveStats: DefensiveStats

        private fun Float.increase(): Float {
            return this * (1f + (incrementPercent - 1) * (1 - (defensiveStats.hp / (defensiveStats.hpMax * hpThresholdPercent))))
        }

        private fun setBaseDamage(min: Boolean) {
            val damageMin = offensiveStats.damageMin
            val damageMax = offensiveStats.damageMax

            if (min && !damageMin.equalsDelta(lastDamageMin)) {
                val newDamage = if (defensiveStats.hp / defensiveStats.hpMax <= hpThresholdPercent)
                    baseDamageMin + damageMin - lastDamageMin
                else
                    damageMin
                baseDamageMin = newDamage
                lastDamageMin = damageMin
            }
            if (!min && !damageMax.equalsDelta(lastDamageMax)) {
                val newDamage = if (defensiveStats.hp / defensiveStats.hpMax <= hpThresholdPercent)
                    damageMax - (lastDamageMax - baseDamageMax)
                else
                    damageMax
                baseDamageMax = newDamage
                lastDamageMax = damageMax
            }
        }

        private val berserkCallable = object : StatsChangedCallable() {
            override fun call(entity: Entity, vararg data: Any?) {
                if (data[0] == StatsEnum.DAMAGE)
                    setBaseDamage(data[2] == "min")
                if (data[0] != StatsEnum.HP)
                    return
                if (defensiveStats.hp / defensiveStats.hpMax <= hpThresholdPercent) {
                    lastDamageMin = baseDamageMin.increase()
                    lastDamageMax = baseDamageMax.increase()
                    offensiveStats.damageMin = lastDamageMin
                    offensiveStats.damageMax = lastDamageMax
                }
                else {
                    lastDamageMin = baseDamageMin
                    lastDamageMax = baseDamageMax
                    offensiveStats.damageMin = baseDamageMin
                    offensiveStats.damageMax = baseDamageMax
                }
            }
        }

        override fun onEntityAttached(entity: Entity) {
            entity.attach(berserkCallable)
            offensiveStats = entity.get(OffensiveStats::class)!!
            defensiveStats = entity.get(DefensiveStats::class)!!
            baseDamageMin = offensiveStats.damageMin
            baseDamageMax = offensiveStats.damageMax
            lastDamageMin = baseDamageMin
            lastDamageMax = baseDamageMax
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(berserkCallable)
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