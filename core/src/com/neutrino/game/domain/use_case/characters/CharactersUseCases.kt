package com.neutrino.game.domain.use_case.characters

import com.neutrino.game.domain.model.turn.CharacterArray

data class CharactersUseCases(
    val characterArray: CharacterArray,
    val getImpassable: GetImpassable = GetImpassable(characterArray)
)