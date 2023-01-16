package com.neutrino.game.domain.model.systems.skills

import com.neutrino.EventDispatcher
import com.neutrino.LevelArrays
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.attack.AreaAttack
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.turn.Turn
import squidpony.squidmath.Coord

class SkillCripplingSpin(override val character: Character): Skill.ActiveSkillTile {
    override val name: String = "Crippling spin"
    override val description: String = "Spinning attack which slowes down nearby enemies"
    override val requirement: RequirementPrintable = RequirementPrintable().add("Strength: 2") { character.strength >= 2 }
    override val textureName: String = "skillCripple"

    override val cooldownLength: Double = 20.0
    override val cooldownType: CooldownType = CooldownType.SKILL(this)

    private val damage = 5f
    private val slowDownStrength = 0.5
    override var range: Int = 2
    override var rangeType: RangeType = RangeType.DIAGONAL

    override val printableData: List<Pair<String, Any>> = listOf(
        Pair("Damage", damage),
        Pair("Slowdown %", slowDownStrength),
        Pair("Range", range),
        Pair("Range type", rangeType)
    )

    override fun use(tile: Coord) {
        val attack = AreaAttack(mapOf(StatsEnum.DAMAGE to damage), range, rangeType)
        attack.attack(character, tile)
        for (coord in getTilesInRange(tile)) {
            val characterAt = LevelArrays.getCharacterAt(coord) ?: continue
            val event = CharacterEvent(characterAt, TimedEvent(OnOffEvent(EventModifyStat(StatsEnum.MOVEMENTSPEED, slowDownStrength, true))), Turn.turn)
            EventDispatcher.dispatchEvent(event)
        }
        causeCooldown()
    }
}