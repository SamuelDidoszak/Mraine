package com.neutrino.game.entities.characters.attributes.util

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.Equipment
import com.neutrino.game.entities.characters.callables.OnItemEquipped
import com.neutrino.game.entities.characters.callables.OnItemUnequipped
import com.neutrino.game.entities.items.attributes.EquipmentItem
import com.neutrino.game.entities.items.attributes.EquipmentType
import com.neutrino.game.entities.items.attributes.HandheldEquipment
import com.neutrino.game.entities.items.attributes.HandheldEquipmentType
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.attack.callables.GotAttackedAfterCallable
import com.neutrino.game.entities.systems.attack.callables.LifestealCallable
import com.neutrino.game.entities.systems.attack.callables.StatsChangedCallable
import com.neutrino.game.entities.systems.attack.util.StatsEnum
import com.neutrino.game.util.equalsDelta
import com.neutrino.game.util.roundOneDecimal

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
                                                                        Defence
     */

    class IncreaseShieldDefence(
        var incrementPercent: Float
    ): CharacterTag {
        private fun setStats(entity: Entity, item: Entity, add: Boolean) {
            if (item.get(HandheldEquipment::class)?.handheldType == HandheldEquipmentType.SHIELD) {
                val modifier = if (add) 1f else -1f
                entity.get(DefensiveStats::class)!!.defence += item.get(DefensiveStats::class)!!.defence * incrementPercent * modifier
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
            val shield = entity.get(Equipment::class)?.getEquipped(Equipment.EquipmentType.LHAND)
            if (shield != null)
                setStats(entity, shield, true)
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(itemEquippedCallable)
            entity.detach(itemUnequippedCallable)
            val weapon = entity.get(Equipment::class)?.getWeapon()
            if (weapon != null)
                setStats(entity, weapon, false)
        }
    }

    class Thorns(
        var damagePercent: Float
    ): CharacterTag {

        val thornsCallable = object : GotAttackedAfterCallable() {
            override fun call(entity: Entity, vararg data: Any?) {
                if (data[1] == null)
                    return
                val damage = ((data[1] as Float) * damagePercent).roundOneDecimal()
                val attack = OffensiveStats(damageMin = damage, damageMax = damage, accuracy = 2f).also { it.entity = entity }
                println("Attacker entity: $${attack.entity}")
                (data[0] as Entity).get(DefensiveStats::class)?.getDamage(attack)
            }
        }

        override fun onEntityAttached(entity: Entity) {
            entity.attach(thornsCallable)
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(thornsCallable)
        }
    }

    class Block(
        var chance: Float
    ): CharacterTag

    class LastManStanding(
        val hpThresholdPercent: Float,
        val incrementPercent: Float
    ): CharacterTag {

        private var baseDefence = 0f
        private var lastDefence = 0f
        private lateinit var defensiveStats: DefensiveStats

        private fun Float.increase(): Float {
            return this * (1f + (incrementPercent - 1) * (1 - (defensiveStats.hp / (defensiveStats.hpMax * hpThresholdPercent))))
        }

        private fun setBaseDamage() {
            val defence = defensiveStats.defence

            if (!defence.equalsDelta(lastDefence)) {
                val newDefence = if (defensiveStats.hp / defensiveStats.hpMax <= hpThresholdPercent)
                    baseDefence + defence - lastDefence
                else
                    defence
                baseDefence = newDefence
                lastDefence = defence
            }
        }

        private val lastManStandingCallable = object : StatsChangedCallable() {
            override fun call(entity: Entity, vararg data: Any?) {
                if (data[0] == StatsEnum.DEFENCE)
                    setBaseDamage()
                if (data[0] != StatsEnum.HP)
                    return
                if (defensiveStats.hp / defensiveStats.hpMax <= hpThresholdPercent) {
                    lastDefence = baseDefence.increase()
                    defensiveStats.defence = lastDefence
                }
                else {
                    lastDefence = baseDefence
                    defensiveStats.defence = baseDefence
                }
            }
        }

        override fun onEntityAttached(entity: Entity) {
            entity.attach(lastManStandingCallable)
            defensiveStats = entity.get(DefensiveStats::class)!!
            baseDefence = defensiveStats.defence
            lastDefence = baseDefence
        }

        override fun onEntityDetached(entity: Entity) {
            entity.detach(lastManStandingCallable)
            defensiveStats.defence = baseDefence
        }
    }

    /** ======================================================================================================================================================
                                                                        Ranged
     */

    class IncreaseArrowDamage(
        var incrementPercent: Float
    ): CharacterTag {

        private fun setStats(entity: Entity, item: Entity, add: Boolean) {
            if (item.get(EquipmentItem::class)?.isRanged() == true) {
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