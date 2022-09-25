package com.neutrino.game.domain.model.characters.utility

interface Stats {
    var hp: Float
    var currentHp: Float
    var mp: Float
    var currentMp: Float
    var attack: Float
    var strength: Float
    var defence: Float
    var agility: Float
    var evasiveness: Float
    var accuracy: Float
    var criticalChance: Float
    var luck: Float
    var attackSpeed: Double
    var movementSpeed: Double
    var range: Int
    var rangeType: RangeType
    var experience: Float

    // environmental
    var fireDamage: Float
    var waterDamage: Float
    var earthDamage: Float
    var airDamage: Float
    var poisonDamage: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var fireDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var waterDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var earthDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var airDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var poisonDefence: Float

    fun setAttack(): Float {
        return strength
    }
}