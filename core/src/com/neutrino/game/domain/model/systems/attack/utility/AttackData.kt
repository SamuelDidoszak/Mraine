package com.neutrino.game.domain.model.systems.attack.utility

import com.neutrino.game.domain.model.characters.Character

data class AttackData (
    val physicalDamage: Float,
    val fireDamage: Float,
    val waterDamage: Float,
    val earthDamage: Float,
    val airDamage: Float,
    val poisonDamage: Float,

    val criticalChance: Float,
    val criticalDamage: Float,

    val accuracy: Float,
    /** Attacking character, used in ai and to decide stealth attacks */
    val character: Character
) {
    fun getDamageSum(): Float {
        return physicalDamage + fireDamage + waterDamage + earthDamage + airDamage + poisonDamage
    }
}