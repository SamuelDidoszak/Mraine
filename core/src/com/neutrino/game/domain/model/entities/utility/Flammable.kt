package com.neutrino.game.domain.model.entities.utility

interface Flammable {
    val isBurnt: Boolean
    val fireResistance: Float
    val burningTime: Float
}