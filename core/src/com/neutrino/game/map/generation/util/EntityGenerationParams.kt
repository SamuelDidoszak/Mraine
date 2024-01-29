package com.neutrino.game.map.generation.util

import com.neutrino.game.map.generation.EntityPositionRequirement
import com.neutrino.game.util.EntityId

data class EntityGenerationParams(
    val id: EntityId,
    val requirements: List<EntityPositionRequirement>,
    val amount: Float,
    val asProbability: Boolean = false,
    val replaceUnderneath: Boolean = false
)
