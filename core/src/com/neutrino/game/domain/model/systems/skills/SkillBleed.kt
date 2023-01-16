package com.neutrino.game.domain.model.systems.skills

import com.neutrino.EventDispatcher
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.attack.BasicAttack
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventBleed
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.turn.Turn

class SkillBleed(override val character: Character): Skill.ActiveSkillCharacter {
    override val name: String = "Bleed"
    override val description: String = "Physical attack that induces bleeding."
    override val requirement: RequirementPrintable = RequirementPrintable().add("Dexterity: 2") { character.dexterity >= 2 }
    override val textureName: String = "skillBleed"

    override val cooldownLength: Double = 20.0
    override val cooldownType: CooldownType = CooldownType.SKILL(this)

    private val bleedDamage = 2f
    private val bleedingLength = 5
    override var range: Int = 1
    override var rangeType: RangeType = RangeType.SQUARE

    override val printableData: List<Pair<String, Any>> = listOf(
        Pair("Bleed damage", bleedDamage),
        Pair("Bleeding length", bleedingLength),
        Pair("Range", range)
    )

    override fun use(target: Character) {
        val attack = BasicAttack(mapOf(StatsEnum.DAMAGE to 0f))
        attack.attack(character, target.getPosition())
        val event = CharacterEvent(target, TimedEvent(0.0, 1.0, bleedingLength, EventBleed(target, bleedDamage)), Turn.turn)
        EventDispatcher.dispatchEvent(event)
        causeCooldown()
    }
}