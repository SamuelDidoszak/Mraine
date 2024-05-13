package com.neutrino.game.entities.systems.requirements

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.graphics.utility.ColorUtils.toHexaDecimal

sealed class Requirements: Attribute() {
    
    abstract fun check(entity: Entity): Boolean
    abstract fun print(entity: Entity): List<Pair<String, String>>

    private companion object {
        val trueColor = "[${ColorUtils.REQ_MET.toHexaDecimal()}]"
        val falseColor = "[${ColorUtils.REQ_UNMET.toHexaDecimal()}]"
    }

    class Custom(
        private val check: (entity: Entity) -> Boolean,
        private val print: String,
        private val printValue: String
    ): Requirements() {
        override fun check(entity: Entity): Boolean = check.invoke(entity)
        override fun print(entity: Entity): List<Pair<String, String>> = listOf(print to printValue)
    }
    
    class Stats(
        val hpMax: Float? = null,
        val hp: Float? = null,
        val mpMax: Float? = null,
        val mp: Float? = null,
        val strength: Float? = null,
        val dexterity: Float? = null,
        val intelligence: Float? = null,
        val luck: Float? = null,
        val damageMin: Float? = null,
        val damageMax: Float? = null,
        val defence: Float? = null,
        /** Range is 0 - 1 which tells the probability of dodging */
        val evasion: Float? = null,
        /** Range is 0 - 2 which tells the probability of hitting the enemy */
        val accuracy: Float? = null,
        val criticalChance: Float? = null,
        /** Damage multiplier applied on critical hit */
        val criticalDamage: Float? = null,
        val attackSpeed: Double? = null,
        val movementSpeed: Double? = null,
//        val range: Int? = null,
//        val rangeType: RangeType? = null,
        val stealth: Float? = null,

        // elemental
        val fireDamageMin: Float? = null,
        val fireDamageMax: Float? = null,
        val waterDamageMin: Float? = null,
        val waterDamageMax: Float? = null,
        val airDamageMin: Float? = null,
        val airDamageMax: Float? = null,
        val poisonDamageMin: Float? = null,
        val poisonDamageMax: Float? = null,
        /** Range is 0 - 2, where 1+ heals instead of damaging */
        val fireDefence: Float? = null,
        /** Range is 0 - 2, where 1+ heals instead of damaging */
        val waterDefence: Float? = null,
        /** Range is 0 - 2, where 1+ heals instead of damaging */
        val airDefence: Float? = null,
        /** Range is 0 - 2, where 1+ heals instead of damaging */
        val poisonDefence: Float? = null
    ): Requirements() {
        override fun check(entity: Entity): Boolean {
            getCheckedFields(entity).forEach { if (!it.third) return false }
            return true
        }

        override fun print(entity: Entity): List<Pair<String, String>> {
            val printList = ArrayList<Pair<String, String>>()
            getCheckedFields(entity).forEach {
                val color = if (it.third) PrintableInfo.betterColor else PrintableInfo.worseColor
                printList.add(color + it.first to color + it.second.toString())
            }
            return printList
        }

        private fun getCheckedFields(entity: Entity): List<Triple<String, Any, Boolean>> {
            val fieldList = ArrayList<Triple<String, Any, Boolean>>()
            this::class.java.declaredFields.forEach {
                if (it.get(this) != null) {
                    var field: Any? = null
                    try {
                        val javaField = OffensiveStats::class.java.getDeclaredField(it.name)
                        javaField.trySetAccessible()
                        field = javaField.get(entity.get(OffensiveStats::class)!!)
                    } catch (_: NoSuchFieldException) { }
                    if (field == null)
                        try {
                            val javaField = DefensiveStats::class.java.getDeclaredField(it.name)
                            javaField.trySetAccessible()
                            field = javaField.get(entity.get(DefensiveStats::class)!!)
                        } catch (_: NoSuchFieldException) { }

                    if (field is Float && (it.get(this) as Float) <= field ||
                        field is Double && (it.get(this) as Double) <= field) {
                        fieldList.add(Triple(it.name.replaceFirstChar { it.uppercase() }, it.get(this), true))
                    } else fieldList.add(Triple(it.name.replaceFirstChar { it.uppercase() }, it.get(this), false))
                }
            }
            return fieldList
        }
    }
}