package com.neutrino.game.domain.use_case.characters

import com.neutrino.game.domain.model.turn.CharacterArray

class UpdateTurnBars(
    private val characterArray: CharacterArray
) {
    operator fun invoke() {
        characterArray.forEach {it.updateTurnBar()}
    }
}