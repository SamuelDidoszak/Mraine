package com.neutrino.game.domain.use_case.characters

import com.neutrino.game.domain.model.characters.Character

class AddCharacter(
    private val characterMap: MutableList<Character>
) {
    operator fun invoke(character: Character) {
        // TODO add sorting by current turn when implemented
        characterMap.add(character)
    }
}