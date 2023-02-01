package com.neutrino.game.domain.use_case.map.utility

import com.neutrino.game.domain.model.entities.utility.Floor
import com.neutrino.game.domain.model.entities.utility.Wall
import kotlin.reflect.KClass

data class EntityParams(
    val wall: KClass<out Wall>,
    val floor: KClass<out Floor>
    )