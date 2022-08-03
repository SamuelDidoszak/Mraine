package com.neutrino.game.domain.model.entities.utility

data class OnMapPosition(
    val map: List<List<MutableList<Entity>>>,
    val xPos: Int,
    val yPos: Int,
    val zPos: Int
)
