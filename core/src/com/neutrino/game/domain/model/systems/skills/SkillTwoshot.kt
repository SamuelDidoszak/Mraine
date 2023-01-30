package com.neutrino.game.domain.model.systems.skills

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import squidpony.squidmath.Coord

class SkillTwoshot(override val character: Character): Skill.ActiveSkillTile {
    override val skillType: SkillType = SkillType.DEXTERITY
    override val name: String = "Twoshot"
    override val description: String = "Uses your primary attack twice"
    override val requirement: RequirementPrintable = RequirementPrintable()
        .add(RequirementPrintable.PrintableReq("Dexterity", 3f) { Player.dexterity })
        { character.dexterity >= 3 }
    override val textureName: String = "skillBleed"

    override val cooldownLength: Double = 20.0
    override val cooldownType: CooldownType = CooldownType.SKILL(this)

    override val manaCost: Float? = null
    override var range: Int = 0
        get() = character.range
    override var rangeType: RangeType = character.rangeType
        get() = character.rangeType

    override val printableData: List<Pair<String, () -> Any>> = listOf(
        Pair("Damage") {character.damage * 2f},
        Pair("Cooldown") {cooldownLength}
    )

    override fun use(tile: Coord) {
        val attack = character.primaryAttack
        attack.attack(character, tile)
        attack.attack(character, tile)
        causeCooldown()
    }
}