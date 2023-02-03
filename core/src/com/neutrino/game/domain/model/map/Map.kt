package com.neutrino.game.domain.model.map

import com.neutrino.game.domain.model.entities.Entity
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
data class Map(
    val xMax: Int,
    val yMax: Int,
    val map: List<List<MutableList<@Polymorphic Entity>>>
)