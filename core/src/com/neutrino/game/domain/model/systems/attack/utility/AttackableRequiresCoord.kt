package com.neutrino.game.domain.model.systems.attack.utility

import squidpony.squidmath.Coord

interface AttackableRequiresCoord: Attackable {
    /**
     * Does nothing
     */
    override fun getDamage(data: AttackData) {

    }

    fun getDamage(data: AttackData, coord: Coord)
}