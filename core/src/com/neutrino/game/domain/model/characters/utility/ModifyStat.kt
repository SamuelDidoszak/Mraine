package com.neutrino.game.domain.model.characters.utility

data class ModifyStat(val stat: StatsEnum, var value: Any, val percent: Boolean = false)