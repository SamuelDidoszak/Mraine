package com.neutrino.game.domain.model.systems.attack.utility

interface Attackable {
    fun getDamage(data: AttackData)
}