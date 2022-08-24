package com.neutrino.game.domain.model.characters.utility

sealed class Action {
    data class MOVE(val x: Int, val y: Int): Action()
    data class ATTACK(val x: Int, val y: Int): Action()
    object SKILL: Action()
    object WAIT: Action()
    object NOTHING: Action()
}