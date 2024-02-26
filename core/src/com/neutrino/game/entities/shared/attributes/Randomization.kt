package com.neutrino.game.entities.shared.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import kotlin.random.Random

open class Randomization(
    private val randomize: (rng: Random, quality: Float, difficulty: Float, entity: Entity) -> Unit
): Attribute() {

    fun randomize(rng: Random, quality: Float, difficulty: Float): Entity {
        randomize.invoke(rng, quality, difficulty, entity)
        return entity
    }
}