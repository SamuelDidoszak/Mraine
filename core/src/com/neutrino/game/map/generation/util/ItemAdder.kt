package com.neutrino.game.map.generation.util

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.items.Item
import com.neutrino.game.entities.items.attributes.GoldValue
import com.neutrino.game.entities.shared.attributes.Randomization
import com.neutrino.game.entities.shared.attributes.RandomizationSimple
import com.neutrino.game.map.generation.TagParams
import kotlin.random.Random

class ItemAdder(
    private val passes: Int? = null, private val itemAmount: Int? = null, private val value: Float? = null,
    private val generator: (items: ArrayList<Item>, itemPool: ItemPool, rng: Random, params: TagParams) -> Boolean
): Attribute() {

    var full = false
    private val itemsAdded = arrayListOf<Item>()
    val items: List<Item>
        get() = itemsAdded

    fun generate(generationParams: GenerationParams) {
        val repeats = passes ?: if (itemAmount != null || value != null) 100 else 1
        for (i in 0 until repeats) {
            if (itemAmount != null && itemAmount == itemsAdded.size)
                break
            if (value != null && itemsAdded.sumOf { it.get(GoldValue::class)!!.value } >= value)
                break

            val added = generator.invoke(itemsAdded, generationParams.interpretedTags.itemPool, generationParams.rng, generationParams.params)
            if (added)
                full = true
        }

        for (item in itemsAdded) {
            item.get(Randomization::class)?.randomize(
                generationParams.rng,
                generationParams.params.itemQuality,
                generationParams.params.difficulty)
            item.get(RandomizationSimple::class)?.randomize(generationParams.rng)
        }
    }
}