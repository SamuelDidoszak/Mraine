package com.neutrino.game.domain.model.characters.utility

data class ModifyStat(val stat: StatsEnum, val value: Any, val percent: Boolean = false)