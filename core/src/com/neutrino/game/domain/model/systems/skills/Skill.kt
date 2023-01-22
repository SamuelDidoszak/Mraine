package com.neutrino.game.domain.model.systems.skills

import com.neutrino.EventDispatcher
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.HasRange
import com.neutrino.game.domain.model.systems.event.CausesCooldown
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.EventCooldown
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.turn.Turn
import squidpony.squidmath.Coord
import kotlin.reflect.KClass

sealed interface Skill {
    val character: Character

    val name: String
    val description: String
    val requirement: RequirementPrintable
    val printableData: List<Pair<String, Any>>
    val textureName: String

    val manaCost: Float?

    interface ActiveSkillCharacter: Skill, CausesCooldown, HasRange {
        fun use(target: Character)
        fun causeCooldown() {
            EventDispatcher.dispatchEvent(
                CharacterEvent(
                    character, Turn.turn, cooldownLength, 1,
                    EventCooldown(character, cooldownType, cooldownLength)
                )
            )
        }
    }

    interface ActiveSkillTile: Skill, CausesCooldown, HasRange {
        fun use(tile: Coord)
        fun causeCooldown() {
            EventDispatcher.dispatchEvent(
                CharacterEvent(
                    character, Turn.turn, cooldownLength, 1,
                    EventCooldown(character, cooldownType, cooldownLength)
                )
            )
        }
    }

    interface ActiveSkillArea: Skill, CausesCooldown, HasRange {
        val area: HasRange
        fun use(tile: Coord)
        fun causeCooldown() {
            EventDispatcher.dispatchEvent(
                CharacterEvent(
                    character, Turn.turn, cooldownLength, 1,
                    EventCooldown(character, cooldownType, cooldownLength)
                )
            )
        }
    }

    interface ActiveSkill: Skill, CausesCooldown {
        fun use()
        fun causeCooldown() {
            EventDispatcher.dispatchEvent(
                CharacterEvent(
                    character, Turn.turn, cooldownLength, 1,
                    EventCooldown(character, cooldownType, cooldownLength)
                )
            )
        }
    }

    interface PassiveSkill: Skill {
        fun useStart()
        fun useStop()

        val playerRequirements: List<Pair<KClass<PassiveSkill>, Boolean>>
    }
}