package com.neutrino.game.entities.characters.attributes.util

import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.util.roundOneDecimal

data class TurnsRemaining(
    val endTurn: Double
) {
    val turnsRemaining: Double
        get() = (endTurn - Turn.turn).roundOneDecimal()
}