package com.neutrino.game.domain.model.items.crystals

import com.neutrino.game.domain.model.items.Item

abstract class Crystal: Item() {
    open val possibleEffectList: List<Any> = listOf()
}