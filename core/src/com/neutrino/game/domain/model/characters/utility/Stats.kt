package com.neutrino.game.domain.model.characters.utility

interface Stats {
    val hp: Float
    var currentHp: Float
    val mp: Float
    var currentMp: Float
    val attack: Float
    val strength: Float
    val defence: Float
    val agility: Float
    val evasiveness: Float
    val accuracy: Float
    val criticalChance: Float
    val luck: Float
    val attackSpeed: Double
    var movementSpeed: Double
    val range: Int
    val rangeType: RangeType
    val experience: Float

    // environmental
    val fireDamage: Float
    val waterDamage: Float
    val earthDamage: Float
    val airDamage: Float
    val poisonDamage: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    val fireDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    val waterDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    val earthDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    val airDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    val poisonDefence: Float

    fun setAttack(): Float {
        return strength
    }
}