package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.Attribute

class OffensiveStats(
    var strength: Float = 0f,
    var dexterity: Float = 0f,
    var intelligence: Float = 0f,
    var luck: Float = 0f,
    var damageMin: Float = 0f,
    var damageMax: Float = damageMin,
    /** Range is 0 - 2 which tells the probability of hitting the enemy */
    var accuracy: Float = 1f,
    var criticalChance: Float = 0f,
    /** Damage multiplier applied on critical hit */
    var criticalDamage: Float = 1f,
    var attackSpeed: Double = 1.0,
    override var range: Int = 1,
    override var rangeType: RangeType = RangeType.SQUARE,
    // elemental
    var fireDamageMin: Float = 0f,
    var fireDamageMax: Float = fireDamageMin,
    var waterDamageMin: Float = 0f,
    var waterDamageMax: Float = waterDamageMin,
    var airDamageMin: Float = 0f,
    var airDamageMax: Float = airDamageMin,
    var poisonDamageMin: Float = 0f,
    var poisonDamageMax: Float = poisonDamageMin
): Attribute(), HasRange