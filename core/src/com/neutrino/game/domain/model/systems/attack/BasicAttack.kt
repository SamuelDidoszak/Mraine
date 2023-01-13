package com.neutrino.game.domain.model.systems.attack

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import squidpony.squidmath.Coord

class BasicAttack: Attack {

    constructor(acceptedDamageTypes: Set<StatsEnum>) : super(acceptedDamageTypes)
    constructor() : super()

    override fun attack(character: Character, target: Coord) {
        getTopmostAttackable(target)?.getDamage(getAttackData(character))
    }
}