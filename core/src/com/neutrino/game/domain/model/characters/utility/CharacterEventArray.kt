package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventCooldown
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent

class CharacterEventArray: ArrayList<CharacterEvent>() {
    /** Number of skills that don't use mana and are on cooldown */
    var skillsOnCooldown: Int = 0

    fun hasCooldown(cooldownType: CooldownType?): Boolean {
        if (cooldownType == null || cooldownType is CooldownType.NONE)
            return false

        return this.find {
            it.event is EventCooldown && it.event.cooldownType == cooldownType &&
                    when (it.event.cooldownType) {
                        is CooldownType.SKILL -> (it.event.cooldownType as CooldownType.SKILL).skill::class == (cooldownType as CooldownType.SKILL).skill::class
                        is CooldownType.ITEM -> (it.event.cooldownType as CooldownType.ITEM)::class == (cooldownType as CooldownType.ITEM)::class
                        is CooldownType.NONE -> false
                        else -> true
                    }
        } != null
    }

    override fun add(element: CharacterEvent): Boolean {
        if (element.event is EventCooldown &&
            element.event.cooldownType is CooldownType.SKILL &&
            (element.event.cooldownType as CooldownType.SKILL).skill.manaCost == null)
            skillsOnCooldown++

        return super.add(element)
    }

    override fun remove(element: CharacterEvent): Boolean {
        if (element.event is EventCooldown &&
            element.event.cooldownType is CooldownType.SKILL &&
            (element.event.cooldownType as CooldownType.SKILL).skill.manaCost == null)
            skillsOnCooldown--

        return super.remove(element)
    }
}