package com.neutrino.game.entities.systems.skills.passive

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.CharacterTags
import com.neutrino.game.entities.characters.attributes.util.CharacterTag
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.equalsDelta

class IncreaseMeleeDamage(caster: Entity, val increment: Float = 1.1f): Skill.PassiveSkill(
    "Increase melee damage",
    "Increase melee damage",
    SkillType.STRENGTH,
    "book",
    caster,
    Requirements.Stats(strength = 2f)
) {

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Damage") + "Additional damage %" to (increment * 100) - 100
    )

    override fun useStart() {
        val previousIncrement = caster.get(CharacterTags::class)?.getTag(CharacterTag.IncreaseOnehandedDamage::class)?.incrementPercent
        caster.get(CharacterTags::class)?.removeTag(CharacterTag.IncreaseOnehandedDamage::class)

        if (caster hasNot CharacterTags::class)
            caster.addAttribute(CharacterTags())

        caster.get(CharacterTags::class)!!.addTag(CharacterTag.IncreaseOnehandedDamage(increment + (previousIncrement ?: 0f)))
    }

    override fun useStop() {
        val previousIncrement = caster.get(CharacterTags::class)!!.getTag(CharacterTag.IncreaseOnehandedDamage::class)!!.incrementPercent
        caster.get(CharacterTags::class)?.removeTag(CharacterTag.IncreaseOnehandedDamage::class)

        if (!(previousIncrement - increment).equalsDelta(0f))
            caster.get(CharacterTags::class)!!.addTag(CharacterTag.IncreaseOnehandedDamage(previousIncrement - increment))
    }
}