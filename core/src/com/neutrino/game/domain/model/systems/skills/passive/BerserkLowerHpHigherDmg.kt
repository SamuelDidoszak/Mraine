package com.neutrino.game.domain.model.systems.skills.passive

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.systems.CharacterTag
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.domain.model.systems.skills.SkillType
import kotlin.reflect.KClass

class BerserkLowerHpHigherDmg(override val character: Character): Skill.PassiveSkill {
    override val skillType: SkillType = SkillType.STRENGTH
    private var hpPercentThreshold: Float = 0.5f
    private var incrementPercent: Float = 2f

    override val playerRequirements: List<Pair<KClass<Skill.PassiveSkill>, Boolean>> = listOf(
        Pair(IncreaseMeleeDamage::class as KClass<Skill.PassiveSkill>, true)
    )

    override val name: String = "Berserk"
    override val description: String = "When below ${(hpPercentThreshold * 100).toInt()}% hp increase damage for up to ${(incrementPercent * 100).toInt()}%"
    override val requirement: RequirementPrintable = RequirementPrintable()
        .add(RequirementPrintable.PrintableReq("Strength", 5f) { Player.strength})
        { (character !is Player) || (character is Player && character.strength >= 5) }

    override val printableData: List<Pair<String, Any>> = listOf(
        Pair("Additional damage %", (incrementPercent * 100) - 100)
    )
    override val textureName: String = "skillTeleportBackstab"
    override val manaCost: Float? = null

    override fun useStart() {
        character.addTag(CharacterTag.BerserkLowerHpHigherDmg(hpPercentThreshold, incrementPercent))
    }

    override fun useStop() {
        character.removeTag(CharacterTag.BerserkLowerHpHigherDmg::class)
    }
}