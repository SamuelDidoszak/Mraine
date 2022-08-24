package com.neutrino.game.domain.use_case.characters

import com.neutrino.game.domain.model.turn.CharacterArray
import squidpony.squidmath.Coord

class GetImpassable(
    private val characterArray: CharacterArray
) {
    operator fun invoke(): Collection<Coord> {
        return characterArray.map { Coord.get(it.xPos, it.yPos) }
    }
}