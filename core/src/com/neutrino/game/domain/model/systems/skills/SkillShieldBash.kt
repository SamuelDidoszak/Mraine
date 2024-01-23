package com.neutrino.game.domain.model.systems.skills

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.attack.BasicAttack
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import squidpony.squidmath.Coord

class SkillShieldBash(override val character: Character): Skill.ActiveSkillTile {
    override val skillType: SkillType = SkillType.STRENGTH
    override val name: String = "Shield bash"
    override val description: String = "Bash your shield into the enemy. Damage is based on DEF"
    override val requirement: RequirementPrintable = RequirementPrintable()
        .add(RequirementPrintable.PrintableReq("Strength", 3f) { Player.strength })
        { character.strength >= 0 }
    override val textureName: String = "skillBleed"

    override val cooldownLength: Double = 20.0
    override val cooldownType: CooldownType = CooldownType.SKILL(this)

    override val manaCost: Float? = null
    private val damage: Float = 2f
    override var range: Int = 1
    override var rangeType: RangeType = RangeType.SQUARE

    override val printableData: List<Pair<String, () -> Any>> = listOf(
        Pair("Damage") { character.defence + damage },
        Pair("Range") { range }
    )

    override fun use(tile: Coord) {
        val attack = BasicAttack(mapOf(StatsEnum.DAMAGE to character.defence - character.damage + damage))
        attack.attack(character, tile)
        causeCooldown()
    }
}