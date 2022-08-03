package com.neutrino.game.domain.model.map

import com.neutrino.game.domain.model.entities.utility.Entity

data class Map(
    val id: Int,
    val name: String,
    val xMax: Int,
    val yMax: Int,
    val map: List<List<MutableList<Entity>>>
)