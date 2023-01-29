package com.neutrino.game.domain.model.systems.skills

import com.neutrino.EventDispatcher
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventManaRegen
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.turn.Turn

class SkillManaDrain(override val character: Character): Skill.ActiveSkillCharacter {
    override val skillType: SkillType = SkillType.INTELLIGENCE
    override val name: String = "ManaDrain"
    override val description: String = "Drain mana from the enemy"
    override val requirement: RequirementPrintable = RequirementPrintable()
        .add(RequirementPrintable.PrintableReq("Intelligence", 5f) { Player.intelligence })
        { character.intelligence >= 5 }
    override val textureName: String = "skillBleed"

    override val cooldownLength: Double = 20.0
    override val cooldownType: CooldownType = CooldownType.SKILL(this)

    override val manaCost: Float? = null
    override var range: Int = 8
    override var rangeType: RangeType = RangeType.CIRCLE
    var manaDrain = 15f

    override val printableData: List<Pair<String, Any>> = listOf(
        Pair("Mana drain", manaDrain),
        Pair("Range", range)
    )

    override fun use(target: Character) {
        val manaDrained =
            if (target.mp > manaDrain)
                manaDrain
            else
                target.mp

        EventDispatcher.dispatchEvent(
            CharacterEvent(target, TimedEvent(1.0, EventManaRegen(-1 * manaDrained)), Turn.turn)
        )

        EventDispatcher.dispatchEvent(
            CharacterEvent(character, TimedEvent(1.0, EventManaRegen(manaDrained)), Turn.turn)
        )

        causeCooldown()
    }
}