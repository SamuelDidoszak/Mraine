package com.neutrino.game.map.generation.worldgen

import com.neutrino.game.entities.items.attributes.ItemData
import com.neutrino.game.entities.items.attributes.tags.ItemTag
import com.neutrino.game.util.EntityName
import kotlin.random.Random

class ItemResolver(
    private val items: List<Pair<String, ItemData>>
) {

    fun getItem(
        query: ItemQuery,
        rng: Random
    ): EntityName? {

        val candidates = items.asSequence()
            .filter { (_, data) ->
                (query.tier == null || data.tier == query.tier) &&
                        query.requiredTags.all { it in data.tagList } &&
                        (query.blockedTags == null || data.tagList.none { it in query.blockedTags })
            }
            .toList()

        if (candidates.isEmpty()) return null

        val weighted = candidates.map { (name, data) ->
            name to computeWeight(data, query.preferredTags)
        }

        return weightedPick(weighted, rng)
    }

    private fun computeWeight(
        data: ItemData,
        preferred: Map<ItemTag, Float>?
    ): Float {
        if (preferred == null) return 1f

        var weight = 1f

        for ((tag, bonus) in preferred) {
            if (tag in data.tagList) {
                weight *= (1f + bonus)
            }
        }

        return weight
    }

    private fun weightedPick(
        entries: List<Pair<EntityName, Float>>,
        rng: Random
    ): EntityName? {
        val total = entries.sumOf { it.second.toDouble() }.toFloat()
        if (total <= 0f) return null

        var roll = rng.nextFloat() * total

        for ((value, weight) in entries) {
            roll -= weight
            if (roll <= 0f) return value
        }

        return null
    }
}
