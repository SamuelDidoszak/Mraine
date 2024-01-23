package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.Attribute

class Stats(
    hpMax: Float = 1f,
    hp: Float = 1f,
    mpMax: Float = 0f,
    mp: Float = 0f,
    strength: Float = 0f,
    dexterity: Float = 0f,
    intelligence: Float = 0f,
    luck: Float = 0f,
    damageMin: Float = 0f,
    damageMax: Float = damageMin,
    defence: Float = 0f,
    /** Range is 0 - 1 which tells the probability of dodging */
    evasion: Float = 0f,
    /** Range is 0 - 2 which tells the probability of hitting the enemy */
    accuracy: Float = 1f,
    criticalChance: Float = 0f,
    /** Damage multiplier applied on critical hit */
    criticalDamage: Float = 1f,
    attackSpeed: Double = 1.0,
    movementSpeed: Double = 1.0,
    range: Int = 1,
    rangeType: RangeType = RangeType.SQUARE,
    stealth: Float = 0f,

    // elemental
    fireDamageMin: Float = 0f,
    fireDamageMax: Float = fireDamageMin,
    waterDamageMin: Float = 0f,
    waterDamageMax: Float = waterDamageMin,
    airDamageMin: Float = 0f,
    airDamageMax: Float = airDamageMin,
    poisonDamageMin: Float = 0f,
    poisonDamageMax: Float = poisonDamageMin,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    fireDefence: Float = 0f,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    waterDefence: Float = 0f,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    airDefence: Float = 0f,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    poisonDefence: Float = 0f
): Attribute() {

    private val defensiveStats: DefensiveStats
    private val offensiveStats: OffensiveStats

    init {
        defensiveStats = DefensiveStats(
            hpMax,
            hp,
            mpMax,
            mp,
            defence,
            evasion,
            movementSpeed,
            stealth,
            fireDefence,
            waterDefence,
            airDefence,
            poisonDefence
        )

        offensiveStats = OffensiveStats(
            strength,
            dexterity,
            intelligence,
            luck,
            damageMin,
            damageMax,
            accuracy,
            criticalChance,
            criticalDamage,
            attackSpeed,
            range,
            rangeType,
            fireDamageMin,
            fireDamageMax,
            waterDamageMin,
            waterDamageMax,
            airDamageMin,
            airDamageMax,
            poisonDamageMin,
            poisonDamageMax
        )
    }

    override fun onEntityAttached() {
        entity.addAttribute(defensiveStats)
        entity.addAttribute(offensiveStats)
    }

    var hpMax: Float
        get() = defensiveStats.hpMax
        set(value) { defensiveStats.hpMax = value }
    var hp: Float
        get() = defensiveStats.hp
        set(value) { defensiveStats.hp = value }
    var mpMax: Float
        get() = defensiveStats.mpMax
        set(value) { defensiveStats.mpMax = value }
    var mp: Float
        get() = defensiveStats.mp
        set(value) { defensiveStats.mp = value }
    var strength: Float
        get() = offensiveStats.strength
        set(value) { offensiveStats.strength = value }
    var dexterity: Float
        get() = offensiveStats.dexterity
        set(value) { offensiveStats.dexterity = value }
    var intelligence: Float
        get() = offensiveStats.intelligence
        set(value) { offensiveStats.intelligence = value }
    var luck: Float
        get() = offensiveStats.luck
        set(value) { offensiveStats.luck = value }
    var damageMin: Float
        get() = offensiveStats.damageMin
        set(value) { offensiveStats.damageMin = value }
    var damageMax: Float
        get() = offensiveStats.damageMax
        set(value) { offensiveStats.damageMax = value }
    var defence: Float
        get() = defensiveStats.defence
        set(value) { defensiveStats.defence = value }
    /** Range is 0 - 1 which tells the probability of dodging */
    var evasion: Float
        get() = defensiveStats.evasion
        set(value) { defensiveStats.evasion = value }
    /** Range is 0 - 2 which tells the probability of hitting the enemy */
    var accuracy: Float
        get() = offensiveStats.accuracy
        set(value) { offensiveStats.accuracy = value }
    var criticalChance: Float
        get() = offensiveStats.criticalChance
        set(value) { offensiveStats.criticalChance = value }
    /** Damage multiplier applied on critical hit */
    var criticalDamage: Float
        get() = offensiveStats.criticalDamage
        set(value) { offensiveStats.criticalDamage = value }
    var attackSpeed: Double
        get() = offensiveStats.attackSpeed
        set(value) { offensiveStats.attackSpeed = value }
    var movementSpeed: Double
        get() = defensiveStats.movementSpeed
        set(value) { defensiveStats.movementSpeed = value }
    var range: Int
        get() = offensiveStats.range
        set(value) { offensiveStats.range = value }
    var rangeType: RangeType
        get() = offensiveStats.rangeType
        set(value) { offensiveStats.rangeType = value }
    var stealth: Float
        get() = defensiveStats.stealth
        set(value) { defensiveStats.stealth = value }

    // elemental
    var fireDamageMin: Float
        get() = offensiveStats.fireDamageMin
        set(value) { offensiveStats.fireDamageMin = value }
    var fireDamageMax: Float
        get() = offensiveStats.fireDamageMax
        set(value) { offensiveStats.fireDamageMax = value }
    var waterDamageMin: Float
        get() = offensiveStats.waterDamageMin
        set(value) { offensiveStats.waterDamageMin = value }
    var waterDamageMax: Float
        get() = offensiveStats.waterDamageMax
        set(value) { offensiveStats.waterDamageMax = value }
    var airDamageMin: Float
        get() = offensiveStats.airDamageMin
        set(value) { offensiveStats.airDamageMin = value }
    var airDamageMax: Float
        get() = offensiveStats.airDamageMax
        set(value) { offensiveStats.airDamageMax = value }
    var poisonDamageMin: Float
        get() = offensiveStats.poisonDamageMin
        set(value) { offensiveStats.poisonDamageMin = value }
    var poisonDamageMax: Float
        get() = offensiveStats.poisonDamageMax
        set(value) { offensiveStats.poisonDamageMax = value }
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var fireDefence: Float
        get() = defensiveStats.fireDefence
        set(value) { defensiveStats.fireDefence = value }
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var waterDefence: Float
        get() = defensiveStats.waterDefence
        set(value) { defensiveStats.waterDefence = value }
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var airDefence: Float
        get() = defensiveStats.airDefence
        set(value) { defensiveStats.airDefence = value }
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var poisonDefence: Float
        get() = defensiveStats.poisonDefence
        set(value) { defensiveStats.poisonDefence = value }
}