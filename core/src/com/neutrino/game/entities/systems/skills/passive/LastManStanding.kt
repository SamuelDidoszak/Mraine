package com.neutrino.game.entities.systems.skills.passive

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.CharacterTags
import com.neutrino.game.entities.characters.attributes.util.CharacterTag
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.equalsDelta
import kotlin.reflect.KClass

class LastManStanding(
    caster: Entity,
    private var hpPercentThreshold: Float = 0.5f,
    private var incrementPercent: Float = 2f): Skill.PassiveSkill(
    "Last man standing",
    "Increase defence as your HP gets lower",
    SkillType.DEFENCE,
    "skillLastManStanding",
    caster,
    Requirements.Stats(strength = 10f)
) {

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Defence") + "Additional defence %" to (incrementPercent * 100).toInt(),
        ColorUtils.getStatColorTextra("Hp") + "Required hp %" to (hpPercentThreshold * 100).toInt()
    )

    override val skillTreeRequirements: List<KClass<out PassiveSkill>> = listOf(IncreaseShieldDefence::class)

    override fun useStart() {
        val previousIncrement = caster.get(CharacterTags::class)?.getTag(CharacterTag.LastManStanding::class)?.incrementPercent
        caster.get(CharacterTags::class)?.removeTag(CharacterTag.LastManStanding::class)

        if (caster hasNot CharacterTags::class)
            caster.addAttribute(CharacterTags())

        caster.get(CharacterTags::class)!!.addTag(CharacterTag.LastManStanding(hpPercentThreshold, incrementPercent + (previousIncrement ?: 0f)))
    }

    override fun useStop() {
        val previousIncrement = caster.get(CharacterTags::class)!!.getTag(CharacterTag.LastManStanding::class)!!.incrementPercent
        caster.get(CharacterTags::class)?.removeTag(CharacterTag.LastManStanding::class)

        if (!(previousIncrement - incrementPercent).equalsDelta(0f))
            caster.get(CharacterTags::class)!!.addTag(CharacterTag.LastManStanding(hpPercentThreshold, previousIncrement - incrementPercent))
    }
}