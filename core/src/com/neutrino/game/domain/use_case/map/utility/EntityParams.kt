package com.neutrino.game.domain.use_case.map.utility

import com.neutrino.game.domain.model.entities.utility.Entity
import kotlin.reflect.KClass

data class EntityParams(
    val wall: KClass<Entity>,
    val floor: KClass<Entity>
    )