package com.neutrino.game.map.generation

import com.neutrino.game.util.EntityName

data class MapTag(
    val tilesets: List<Tileset>,
    val mapGenerators: List<Generator>,
    val characterList: List<EntityName>,
    val itemList: List<Pair<Float, EntityName>>,
    val tagParams: TagParams,
    val isModifier: Boolean
)
