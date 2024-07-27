package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.events.Cooldown
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.requirements.PrintableInfo
import com.neutrino.game.entities.systems.requirements.Requirements
import kotlin.reflect.KClass

sealed class Skill: PrintableInfo<Skill> {
    abstract val caster: Entity
    abstract val skillType: SkillType

    abstract val name: String
    abstract val description: String
    abstract val requirements: List<Requirements>?
    abstract val textureName: String

    abstract val manaCost: Float?
    abstract val cooldown: Double

    init {
        requirements?.forEach { it.entity = caster }
    }

    fun causeCooldown() {
        Events.addEvent(caster, Cooldown(
            caster, Cooldown.Type.SKILL(this), cooldown
            ).asTimedEvent())
    }

    protected fun playAnimation(name: String) {
        (caster as Character).setAnimation(name, "idle")
    }

    abstract class ActiveSkillCharacter(
        override val name: String,
        override val description: String,
        override val skillType: SkillType,
        override val textureName: String,
        override val manaCost: Float?,
        override val cooldown: Double,
        override val caster: Entity,
        override var range: Int,
        override var rangeType: RangeType = RangeType.SQUARE,
        vararg requirements: Requirements
    ): Skill(), HasRange {
        override val requirements: List<Requirements>? = requirements.toList()
        abstract fun use(target: Character)
    }

    abstract class ActiveSkillEntity(
        override val name: String,
        override val description: String,
        override val skillType: SkillType,
        override val textureName: String,
        override val manaCost: Float?,
        override val cooldown: Double,
        override val caster: Entity,
        override var range: Int,
        override var rangeType: RangeType = RangeType.SQUARE,
        vararg requirements: Requirements
    ): Skill(), HasRange {
        override val requirements: List<Requirements>? = requirements.toList()
        abstract fun use(target: Entity)
    }

    abstract class ActiveSkillPosition(
        override val name: String,
        override val description: String,
        override val skillType: SkillType,
        override val textureName: String,
        override val manaCost: Float?,
        override val cooldown: Double,
        override val caster: Entity,
        override var range: Int,
        override var rangeType: RangeType = RangeType.SQUARE,
        vararg requirements: Requirements
    ): Skill(), HasRange {
        override val requirements: List<Requirements>? = requirements.toList()
        abstract fun use(target: Position)
    }

    abstract class ActiveSkillArea(
        override val name: String,
        override val description: String,
        override val skillType: SkillType,
        override val textureName: String,
        override val manaCost: Float?,
        override val cooldown: Double,
        override val caster: Entity,
        override var range: Int,
        override var rangeType: RangeType = RangeType.SQUARE,
        vararg requirements: Requirements
    ): Skill(), HasRange {
        override val requirements: List<Requirements>? = requirements.toList()
        abstract val area: HasRange
        abstract fun use(target: Position)
    }

    abstract class ActiveSkill(
        override val name: String,
        override val description: String,
        override val skillType: SkillType,
        override val textureName: String,
        override val manaCost: Float?,
        override val cooldown: Double,
        override val caster: Entity,
        vararg requirements: Requirements
    ): Skill() {
        override val requirements: List<Requirements>? = requirements.toList()
        abstract fun use()
    }

    abstract class PassiveSkill(
        override val name: String,
        override val description: String,
        override val skillType: SkillType,
        override val textureName: String,
        override val caster: Entity,
        vararg requirements: Requirements
    ): Skill() {
        override val manaCost: Float? = null
        override val cooldown: Double = 0.0
        override val requirements: List<Requirements>? = requirements.toList()
        open val skillTreeRequirements: List<KClass<out PassiveSkill>> = listOf()
        abstract fun useStart()
        abstract fun useStop()
    }
}