package com.neutrino.game.map.generation.worldgen

import com.neutrino.game.entities.Attribute
import kotlin.random.Random

class LootTable(
    private vararg val entries: LootVal
): Attribute() {

    fun rollLootEntry(random: Random): LootVal? {
        val probabilityMap = entries.map { entry -> entry to entry.probability }
        var totalProbability = 0f
        for (element in probabilityMap) {
            totalProbability += element.second
        }

        if (totalProbability <= 0f) return null

        var roll = random.nextFloat() * totalProbability
        for ((value, probability) in probabilityMap) {
            roll -= probability
            if (roll <= 0f) return value
        }

        return null
    }

}