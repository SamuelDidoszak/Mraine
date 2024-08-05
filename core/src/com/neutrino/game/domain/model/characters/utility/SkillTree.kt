package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.entities.systems.skills.passive.*
import com.neutrino.game.util.round

enum class SkillTree(val skills: List<List<Skill.PassiveSkill>>) {
    STRENGTH(listOf(
        listOf(Pad(1f), IncreaseMeleeDamage(Player), Pad(2f), ActiveShield(Player), Pad(1f)),
        listOf(Pad(0.33f), IncreaseOnehandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), Pad(1f), IncreaseShieldDefence(Player), Pad(1f)),
        listOf(Pad(1f), Berserk(Player), Pad(2f), LastManStanding(Player), Pad(1f)),
        listOf(Pad(3f), Pad(0.33f), Thorns(Player), Pad(0.33f), Block(Player), Pad(0.33f))
    )),

    DEXTERITY(listOf(
        listOf(Pad(1f), IncreaseTwohandedDamage(Player), Pad(2f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(Pad(2.5f), IncreaseMeleeDamage(Player), Pad(2.5f)),
        listOf(Pad(1f), IncreaseOnehandedDamage(Player), Pad(2f), Berserk(Player), Pad(0.33f)),
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(IncreaseTwohandedDamage(Player), IncreaseTwohandedDamage(Player), IncreaseTwohandedDamage(Player), Pad(1f), IncreaseTwohandedDamage(Player))
    )),

    INTELLIGENCE(listOf(
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(Pad(2.5f), IncreaseMeleeDamage(Player), Pad(2.5f)),
        listOf(Pad(1f), IncreaseOnehandedDamage(Player), Pad(2f), Berserk(Player), Pad(0.33f)),
    )),

    SUMMONING(listOf(
        listOf(Pad(2.5f), IncreaseMeleeDamage(Player), Pad(2.5f)),
        listOf(Pad(1f), IncreaseOnehandedDamage(Player), Pad(2f), Berserk(Player), Pad(0.33f)),
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.66f), IncreaseTwohandedDamage(Player), Pad(0.33f), IncreaseTwohandedDamage(Player), Pad(0.33f)),
        listOf(IncreaseTwohandedDamage(Player), IncreaseTwohandedDamage(Player), IncreaseTwohandedDamage(Player), Pad(1f), IncreaseTwohandedDamage(Player))
    )),;

    class Pad(private val cells: Float): Skill.PassiveSkill(
        "Pad",
        "Pad passiveSkill is only used in skill tree UI",
        SkillType.INTELLIGENCE,
        "Pad passiveSkill is only used in skill tree UI",
        entity
    ) {
        fun get(): Float {
            return (cells * 84f).round()
        }

        companion object {
            const val BETWEEN_TWO: Float = 0.5f + 0.33f / 2
            private val entity = Entity()
        }

        override fun useStart() {
            throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        }

        override fun useStop() {
            throw NotImplementedError("Pad passiveSkill is only used in skill tree UI")
        }

        override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> {
            TODO("Not yet implemented")
        }
    }
}