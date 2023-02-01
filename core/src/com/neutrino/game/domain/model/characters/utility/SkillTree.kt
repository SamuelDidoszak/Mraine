package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.domain.model.systems.skills.SkillType
import com.neutrino.game.domain.model.systems.skills.passive.BerserkLowerHpHigherDmg
import com.neutrino.game.domain.model.systems.skills.passive.IncreaseMeleeDamage
import com.neutrino.game.domain.model.systems.skills.passive.IncreaseOnehandedDamage
import com.neutrino.game.domain.model.systems.skills.passive.IncreaseTwohandedDamage
import com.neutrino.game.round
import kotlin.reflect.KClass

enum class SkillTree(val skills: List<List<Skill.PassiveSkill>>) {
    STRENGTH(listOf(
        listOf(Pad(2.5f), IncreaseMeleeDamage(Player), Pad(2.5f)),
        listOf(Pad(1f), IncreaseOnehandedDamage(Player), Pad(2f), BerserkLowerHpHigherDmg(Player), Pad(0.33f)),
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(IncreaseTwohandedDamage(Player), IncreaseTwohandedDamage(Player), IncreaseTwohandedDamage(Player), Pad(1f), IncreaseTwohandedDamage(Player))
    )),

    DEXTERITY(listOf(
        listOf(Pad(1f), IncreaseTwohandedDamage(Player), Pad(2f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(Pad(2.5f), IncreaseMeleeDamage(Player), Pad(2.5f)),
        listOf(Pad(1f), IncreaseOnehandedDamage(Player), Pad(2f), BerserkLowerHpHigherDmg(Player), Pad(0.33f)),
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(IncreaseTwohandedDamage(Player), IncreaseTwohandedDamage(Player), IncreaseTwohandedDamage(Player), Pad(1f), IncreaseTwohandedDamage(Player))
    )),

    INTELLIGENCE(listOf(
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(Pad(2.5f), IncreaseMeleeDamage(Player), Pad(2.5f)),
        listOf(Pad(1f), IncreaseOnehandedDamage(Player), Pad(2f), BerserkLowerHpHigherDmg(Player), Pad(0.33f)),
    )),

    SUMMONING(listOf(
        listOf(Pad(2.5f), IncreaseMeleeDamage(Player), Pad(2.5f)),
        listOf(Pad(1f), IncreaseOnehandedDamage(Player), Pad(2f), BerserkLowerHpHigherDmg(Player), Pad(0.33f)),
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(IncreaseTwohandedDamage(Player), IncreaseTwohandedDamage(Player), IncreaseTwohandedDamage(Player), Pad(1f), IncreaseTwohandedDamage(Player))
    )),;

    class Pad(private val cells: Float): Skill.PassiveSkill {
        fun get(): Float {
            return (cells * 84f).round()
        }

        companion object {
            const val BETWEEN_TWO: Float = 0.5f + 0.33f / 2
        }

        override fun useStart() {
            throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        }

        override fun useStop() {
            throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        }

        override val skillType: SkillType
            get() = throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        override val playerRequirements: List<Pair<KClass<Skill.PassiveSkill>, Boolean>>
            get() = throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        override val character: Character
            get() = throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        override val name: String
            get() = throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        override val description: String
            get() = throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        override val requirement: RequirementPrintable
            get() = throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        override val printableData: List<Pair<String, () -> Any>>
            get() = throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        override val textureName: String
            get() = throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        override val manaCost: Float?
            get() = throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
    }
}