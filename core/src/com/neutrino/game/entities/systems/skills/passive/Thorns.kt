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

class Thorns(caster: Entity, val damagePercent: Float = 0.15f): Skill.PassiveSkill(
    "Thorns",
    "Attackers get damage",
    SkillType.DEFENCE,
    "skillThorns",
    caster,
    Requirements.Stats(strength = 4f)
) {

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Damage") + "Reflected damage %" to (damagePercent * 100).toInt()
    )

    override val skillTreeRequirements: List<KClass<out PassiveSkill>> = listOf(LastManStanding::class)

    override fun useStart() {
        val previousIncrement = caster.get(CharacterTags::class)?.getTag(CharacterTag.Thorns::class)?.damagePercent
        caster.get(CharacterTags::class)?.removeTag(CharacterTag.Thorns::class)

        if (caster hasNot CharacterTags::class)
            caster.addAttribute(CharacterTags())

        caster.get(CharacterTags::class)!!.addTag(CharacterTag.Thorns(damagePercent + (previousIncrement ?: 0f)))
    }

    override fun useStop() {
        val previousIncrement = caster.get(CharacterTags::class)!!.getTag(CharacterTag.Thorns::class)!!.damagePercent
        caster.get(CharacterTags::class)?.removeTag(CharacterTag.Thorns::class)

        if (!(previousIncrement - damagePercent).equalsDelta(0f))
            caster.get(CharacterTags::class)!!.addTag(CharacterTag.Thorns(previousIncrement - damagePercent))
    }
}