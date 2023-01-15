package com.neutrino.game.domain.model.systems.attack

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.HasRange
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import squidpony.squidmath.Coord
import kotlin.properties.Delegates

class AreaAttack: Attack, HasRange {
    constructor(acceptedDamageTypes: Map<StatsEnum, Float>, range: Int, rangeType: RangeType) : super(acceptedDamageTypes) {
        this.range = range
        this.rangeType = rangeType
    }

    constructor(range: Int, rangeType: RangeType) : super() {
        this.range = range
        this.rangeType = rangeType
    }

    constructor(acceptedDamageTypes: Map<StatsEnum, Float>, hasRange: HasRange) : super(acceptedDamageTypes) {
        this.range = hasRange.range
        this.rangeType = hasRange.rangeType
    }

    constructor(hasRange: HasRange) : super() {
        this.range = hasRange.range
        this.rangeType = hasRange.rangeType
    }

    override var range by Delegates.notNull<Int>()
    override lateinit var rangeType: RangeType

    override fun attack(character: Character, target: Coord) {
        val attackData = getAttackData(character)
        for (tile in getTilesInRange(target)) {
            for (attackable in getAllAttackable(tile)) {
                attackable.getDamage(attackData)
            }
        }
    }
}