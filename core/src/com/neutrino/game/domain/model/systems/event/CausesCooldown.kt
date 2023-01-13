package com.neutrino.game.domain.model.systems.event

import com.neutrino.game.domain.model.systems.event.types.CooldownType

interface CausesCooldown {
    val cooldownType: CooldownType
    val cooldownLength: Double
}