package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.AttributeOperations
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.shared.util.RangeType

class AreaAttack(
    override var range: Int,
    override var rangeType: RangeType): Attribute(), HasRange, AttributeOperations<AreaAttack> {

    override fun plus(other: AreaAttack): AreaAttack = plusPrevious(other)
    override fun minus(other: AreaAttack): AreaAttack = minusPrevious(other)
    override fun shouldRemove(): Boolean = range == 0
    override fun clone(): AreaAttack = AreaAttack(range, rangeType)
    override fun isEqual(other: AreaAttack): Boolean = range == other.range && rangeType == other.rangeType
}