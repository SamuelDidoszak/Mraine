package com.neutrino.game.domain.model.turn

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.util.InteractionType
import squidpony.squidmath.Coord

sealed class Action {
    data class MOVE(val x: Int, val y: Int): Action()
    data class ATTACK(val x: Int, val y: Int): Action()
    data class INTERACTION(val entity: Entity, val interaction: InteractionType): Action()
    data class ITEM(val item: Entity, val targetEntity: Entity? = null, val targetPosition: Position? = null): Action()
    data class SKILL(val skill: Skill, val target: Character? = null, val tile: Coord? = null): Action()
    object EVENT: Action()
    object WAIT: Action()
    object NOTHING: Action()
}