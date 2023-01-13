package com.neutrino.game.domain.model.systems.attack.utility

data class AttackData (
    val physicalDamage: Float,
    val fireDamage: Float,
    val waterDamage: Float,
    val earthDamage: Float,
    val airDamage: Float,
    val poisonDamage: Float,

    val accuracy: Float
) {
    fun getDamageSum(): Float {
        return physicalDamage + fireDamage + waterDamage + earthDamage + airDamage + poisonDamage
    }
}