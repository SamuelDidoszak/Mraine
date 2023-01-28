package com.neutrino.game.domain.model.systems.skills

import com.neutrino.EventDispatcher
import com.neutrino.LevelArrays
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.HasRange
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.attack.BasicAttack
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventBurn
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.turn.Turn
import squidpony.squidmath.Coord

class SkillMeteorite(override val character: Character): Skill.ActiveSkillArea {
    override val skillType: SkillType = SkillType.INTELLIGENCE
    override val name: String = "Meteorite"
    override val description: String = "Summon a small celestial body to aid you in demolishing your enemies"
    override val requirement: RequirementPrintable = RequirementPrintable()
        .add(RequirementPrintable.PrintableReq("Intelligence", 10f) { Player.intelligence })
        { character.intelligence >= 10 }
    override val textureName: String = "skillBleed"

    override val cooldownLength: Double = 20.0
    override val cooldownType: CooldownType = CooldownType.SKILL(this)

    private val fireDamage: Float = 100f
    private val burnDamage: Float = 5f
    private val burnLength: Double = 10.0
    override val manaCost: Float? = 10f
    override val area: HasRange = object: HasRange {
        override var range: Int = 5
        override var rangeType: RangeType = RangeType.CIRCLE
    }
    override var range: Int = 10
    override var rangeType: RangeType = RangeType.CIRCLE

    override val printableData: List<Pair<String, Any>> = listOf(
        Pair("Fire damage", fireDamage),
        Pair("Burn damage", burnDamage),
        Pair("Burn length", burnLength),
        Pair("Range", range)
    )

    override fun use(tile: Coord) {
        for (tile in area.getTilesInRange(tile)) {
            val attack = BasicAttack(mapOf(StatsEnum.FIREDAMAGE to fireDamage))
            attack.attack(character, tile)
            val enemy = LevelArrays.getCharacterAt(tile)
            if (enemy != null) {
                val event = CharacterEvent(enemy, TimedEvent(0.0, 1.0, burnLength.toInt(), EventBurn(enemy, burnDamage)), Turn.turn)
                EventDispatcher.dispatchEvent(event)
            }
        }
        causeCooldown()
    }
}