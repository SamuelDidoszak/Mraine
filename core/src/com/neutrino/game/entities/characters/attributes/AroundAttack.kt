package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.AttributeOperations

class AroundAttack: Attribute(), AttributeOperations<AroundAttack> {

    override fun plus(other: AroundAttack): AroundAttack = plusPrevious(other)
    override fun minus(other: AroundAttack): AroundAttack = minusPrevious(other)
    override fun clone(): AroundAttack = AroundAttack()
    override fun isEqual(other: AroundAttack): Boolean = true
}