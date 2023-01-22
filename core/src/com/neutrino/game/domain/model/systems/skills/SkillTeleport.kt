package com.neutrino.game.domain.model.systems.skills

import com.neutrino.EventDispatcher
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventTeleport
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.turn.Turn
import squidpony.squidmath.Coord

class SkillTeleport(override val character: Character): Skill.ActiveSkillTile {
    override val skillType: SkillType = SkillType.INTELLIGENCE
    override val name: String = "Teleport"
    override val description: String = "Teleports you away"
    override val requirement: RequirementPrintable = RequirementPrintable()
        .add(RequirementPrintable.PrintableReq("Intelligence", 5f) { Player.intelligence })
        { character.intelligence >= 5 }
    override val textureName: String = "skillBleed"

    override val cooldownLength: Double = 2.0
    override val cooldownType: CooldownType = CooldownType.SKILL(this)

    override val manaCost: Float? = 5f
    override var range: Int = 20
    override var rangeType: RangeType = RangeType.SQUARE

    override val printableData: List<Pair<String, Any>> = listOf(
        Pair("Range", range)
    )

    override fun use(tile: Coord) {
        val event = CharacterEvent(character, OnOffEvent(EventTeleport(character, tile)), Turn.turn)
        EventDispatcher.dispatchEvent(event)
        causeCooldown()
    }
}