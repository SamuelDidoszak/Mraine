package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.util.AttributeOperations

class AroundAttack: Attribute(), AttributeOperations<AroundAttack> {

    override fun plusEquals(other: AroundAttack) = plusPrevious(other)
    override fun minusEquals(other: AroundAttack) = minusPrevious(other)
    override fun clone(): AroundAttack = AroundAttack()
    override fun isEqual(other: AroundAttack): Boolean = true
}