package com.neutrino.game.domain.model.entities.utility

class Action(var actionName: String, var requiredDistance: Int, val act: () -> Unit) {
}