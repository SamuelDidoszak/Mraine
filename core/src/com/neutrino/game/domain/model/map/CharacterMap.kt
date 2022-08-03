package com.neutrino.game.domain.model.map

data class CharacterMap(
    val id: Int?,
    val xMax: Int,
    val yMax: Int,
    val map: List<List<Character>>
)
