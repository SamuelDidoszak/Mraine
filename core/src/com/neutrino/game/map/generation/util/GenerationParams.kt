package com.neutrino.game.map.generation.util

import com.neutrino.game.entities.Entity
import com.neutrino.game.map.generation.MapTagInterpretation
import kotlin.random.Random

data class GenerationParams(
    val interpretedTags: MapTagInterpretation,
    val rng: Random,
    var map: List<List<MutableList<Entity>>>
)
