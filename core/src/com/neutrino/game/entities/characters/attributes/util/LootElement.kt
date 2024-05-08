package com.neutrino.game.entities.characters.attributes.util

import com.neutrino.game.entities.Entity
import kotlin.random.Random

data class LootElement(
    val item: String,
    val afterGeneration: (entity: Entity, rng: Random) -> Unit = { _, _ -> },
    val probability: Float
) {
    constructor(item: String, probability: Float): this(item, { _, _ -> }, probability)
}