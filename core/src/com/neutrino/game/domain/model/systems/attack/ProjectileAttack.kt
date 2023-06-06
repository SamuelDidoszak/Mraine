package com.neutrino.game.domain.model.systems.attack

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.utility.HasProjectile
import com.neutrino.game.domain.model.systems.attack.utility.AttackableRequiresCoord

import squidpony.squidmath.Coord

//class ProjectileAttack(
//    val hasProjectile: HasProjectile
//): Attack() {

class ProjectileAttack: Attack {

    constructor(hasProjectile: HasProjectile, acceptedDamageTypes: Map<StatsEnum, Float>) : super(acceptedDamageTypes) {
        this.hasProjectile = hasProjectile
    }
    constructor(hasProjectile: HasProjectile) : super() {
        this.hasProjectile = hasProjectile
    }
    lateinit var hasProjectile: HasProjectile

    override fun attack(character: Character, target: Coord) {
        hasProjectile.shoot(character.xPos, character.yPos, target.x, target.y, character.parent.parent)
        val attackable = getTopmostAttackable(target)
        if (attackable !is AttackableRequiresCoord)
            attackable?.getDamage(getAttackData(character))
        else
            attackable.getDamage(getAttackData(character), target)
    }
}