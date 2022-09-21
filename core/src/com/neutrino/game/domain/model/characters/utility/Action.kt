package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.domain.model.items.Item

sealed class Action {
    data class MOVE(val x: Int, val y: Int): Action()
    data class ATTACK(val x: Int, val y: Int): Action()
    data class PICKUP(val x: Int, val y: Int): Action()
    data class ITEM(val item: Item): Action()
    object SKILL: Action()
    object WAIT: Action()
    object NOTHING: Action()
}