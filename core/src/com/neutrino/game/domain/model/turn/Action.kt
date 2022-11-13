package com.neutrino.game.domain.model.turn

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.Interaction
import com.neutrino.game.domain.model.items.Item

sealed class Action {
    data class MOVE(val x: Int, val y: Int): Action()
    data class ATTACK(val x: Int, val y: Int): Action()
    data class INTERACTION(val entity: Entity, val interaction: Interaction): Action()
    data class ITEM(val item: Item, val character: Character): Action()
    object SKILL: Action()
    object EVENT: Action()
    object WAIT: Action()
    object NOTHING: Action()
}