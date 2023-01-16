package com.neutrino.game.domain.model.systems.attack

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.attack.utility.AttackableRequiresCoord
import squidpony.squidmath.Coord

class BasicAttack: Attack {

    constructor(acceptedDamageTypes: Map<StatsEnum, Float>) : super(acceptedDamageTypes)
    constructor() : super()

    override fun attack(character: Character, target: Coord) {
        val attackable = getTopmostAttackable(target)
        if (attackable !is AttackableRequiresCoord)
            attackable?.getDamage(getAttackData(character))
        else
            attackable.getDamage(getAttackData(character), target)
    }
}