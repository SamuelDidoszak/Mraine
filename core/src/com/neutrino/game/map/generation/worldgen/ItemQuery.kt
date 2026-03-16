package com.neutrino.game.map.generation.worldgen

import com.neutrino.game.entities.items.attributes.tags.ItemTag

data class ItemQuery(
    val requiredTags: Set<ItemTag>,
    val tier: Int? = null,
    val preferredTags: Map<ItemTag, Float>? = null,
    val blockedTags: Set<ItemTag>? = null
)
