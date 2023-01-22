package com.neutrino.game.domain.model.systems.skills

import com.neutrino.EventDispatcher
import com.neutrino.LevelArrays
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.HasRange
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.characters.utility.SkillTree
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.attack.AroundAttack
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.turn.Turn

class SkillCripplingSpin(override val character: Character): Skill.ActiveSkill, HasRange {
    override val skillType: SkillType = SkillType.STRENGTH
    override val name: String = "Crippling spin"
    override val description: String = "Spinning attack which slows down nearby enemies"
    override val requirement: RequirementPrintable = RequirementPrintable()
        .add(RequirementPrintable.PrintableReq("Strength", 2f) { Player.strength }) {
            character.strength >= 2
        }
    override val textureName: String = "skillCripple"

    override val cooldownLength: Double = 20.0
    override val cooldownType: CooldownType = CooldownType.SKILL(this)

    override val manaCost: Float? = null
    private val damage = 5f
    private val slowDownStrength = 2.0
    private val slowDownTime = 10.0
    override var range: Int = 2
    override var rangeType: RangeType = RangeType.SQUARE

    override val printableData: List<Pair<String, Any>> = listOf(
        Pair("Damage", damage),
        Pair("Slowdown %", slowDownStrength),
        Pair("Slowdown time", slowDownTime),
        Pair("Range", range),
        Pair("Range type", rangeType)
    )

    override fun use() {
        val attack = AroundAttack(mapOf(StatsEnum.DAMAGE to damage), range, rangeType)
        attack.attack(character)
        for (coord in getTilesInRange(character.getPosition(), true)) {
            val characterAt = LevelArrays.getCharacterAt(coord) ?: continue
            val event = CharacterEvent(characterAt, TimedEvent(slowDownTime, EventModifyStat(StatsEnum.MOVEMENTSPEED, slowDownStrength, true)), Turn.turn)
            EventDispatcher.dispatchEvent(event)
        }
        causeCooldown()
    }
}