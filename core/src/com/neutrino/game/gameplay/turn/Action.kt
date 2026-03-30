package com.neutrino.game.gameplay.turn

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map_entities.util.Interactable
import com.neutrino.game.entities.systems.skills.Skill

sealed class Action {
    data class MOVE(val position: Position): Action()
    data class ATTACK(val x: Int, val y: Int): Action()
    data class INTERACTION(val entity: Entity, val interaction: Interactable): Action()
    data class ITEM(val item: Entity, val targetEntity: Entity? = null, val targetPosition: Position? = null): Action()
    data class SKILL(val skill: Skill, val data: Any?): Action()
    object EVENT: Action()
    object WAIT: Action()
    data class WAITSKILL(val skill: Skill.ActivatedPassiveSkill? = null): Action()
    object NOTHING: Action()
}