package com.neutrino.game.map.generation

import com.neutrino.game.util.EntityName
import com.neutrino.game.utility.Probability

data class MapTag(
    val tilesets: List<Tileset>,
    val mapGenerators: List<Generator>,
    val characterList: List<EntityName>,
    val itemList: List<Probability<EntityName>>,
    val tagParams: TagParams,
    val isModifier: Boolean
)
