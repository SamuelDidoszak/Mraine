package com.neutrino.game.entities.systems.skills.passive

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.CharacterTags
import com.neutrino.game.entities.characters.attributes.Skills
import com.neutrino.game.entities.characters.attributes.util.CharacterTag
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.equalsDelta
import kotlin.reflect.KClass

class Berserk(caster: Entity): Skill.PassiveSkill(
    "Berserk",
    "Increase damage on lower hp",
    SkillType.STRENGTH,
    "book",
    caster,
    Requirements.Stats(strength = 5f),
    Requirements.Custom({entity: Entity ->
        entity.get(Skills::class)?.has(IncreaseMeleeDamage::class) == true
    }, "Learned IncreaseMeleeDamage", "")
) {

    private var hpPercentThreshold: Float = 0.5f
    private var incrementPercent: Float = 2f

    override val description: String = "When below ${(hpPercentThreshold * 100).toInt()}% hp increase damage for up to ${(incrementPercent * 100).toInt()}%"

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Damage") + "Additional damage %" to (incrementPercent * 100).toInt(),
        ColorUtils.getStatColorTextra("Hp") + "Required hp %" to (hpPercentThreshold * 100).toInt()
    )

    override val skillTreeRequirements: List<KClass<out PassiveSkill>> = listOf(IncreaseMeleeDamage::class)

    override fun useStart() {
        val previousIncrement = caster.get(CharacterTags::class)!!.getTag(CharacterTag.Berserk::class)?.incrementPercent
        caster.get(CharacterTags::class)?.removeTag(CharacterTag.Berserk::class)

        if (caster hasNot CharacterTags::class)
            caster.addAttribute(CharacterTags())

        caster.get(CharacterTags::class)!!
            .addTag(CharacterTag.Berserk(hpPercentThreshold, incrementPercent + (previousIncrement ?: 0f)))
    }

    override fun useStop() {
        val previousIncrement = caster.get(CharacterTags::class)!!.getTag(CharacterTag.Berserk::class)!!.incrementPercent
        caster.get(CharacterTags::class)?.removeTag(CharacterTag.Berserk::class)

        if (!(previousIncrement - incrementPercent).equalsDelta(0f))
            caster.get(CharacterTags::class)!!
                .addTag(CharacterTag.Berserk(hpPercentThreshold,previousIncrement - incrementPercent))
    }
}