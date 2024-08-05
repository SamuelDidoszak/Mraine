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

class Block(caster: Entity, val blockChance: Float = 0.15f): Skill.PassiveSkill(
    "Block",
    "Chance to block an attack",
    SkillType.DEFENCE,
    "skillBlock",
    caster,
    Requirements.Stats(strength = 7f)
) {

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Defence") + "Block chance" to (blockChance * 100).toInt()
    )

    override val skillTreeRequirements: List<KClass<out PassiveSkill>> = listOf(LastManStanding::class)

    override fun useStart() {
        val previousIncrement = caster.get(CharacterTags::class)?.getTag(CharacterTag.Block::class)?.chance
        caster.get(CharacterTags::class)?.removeTag(CharacterTag.Block::class)

        if (caster hasNot CharacterTags::class)
            caster.addAttribute(CharacterTags())

        caster.get(CharacterTags::class)!!.addTag(CharacterTag.Block(blockChance + (previousIncrement ?: 0f)))
    }

    override fun useStop() {
        val previousIncrement = caster.get(CharacterTags::class)!!.getTag(CharacterTag.Block::class)!!.chance
        caster.get(CharacterTags::class)?.removeTag(CharacterTag.Block::class)

        if (!(previousIncrement - blockChance).equalsDelta(0f))
            caster.get(CharacterTags::class)!!.addTag(CharacterTag.Block(previousIncrement - blockChance))
    }
}