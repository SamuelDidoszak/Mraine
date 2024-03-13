package com.neutrino.game.entities.shared.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import kotlin.random.Random

class RandomizationSimple(
    private val randomize: (rng: Random, entity: Entity) -> Unit
): Attribute() {
    fun randomize(rng: Random): Entity {
        randomize.invoke(rng, entity)
        return entity
    }
}