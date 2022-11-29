package com.neutrino.game.domain.model.event

import com.neutrino.game.domain.model.event.types.CooldownType

interface CausesCooldown {
    val cooldownType: CooldownType
    val cooldownLength: Double
}