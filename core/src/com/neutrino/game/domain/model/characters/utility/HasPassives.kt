package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.domain.model.systems.skills.Skill
import kotlin.reflect.KClass
import kotlin.reflect.cast

interface HasPassives {
    val passives: HashMap<KClass<out Skill.PassiveSkill>, Skill.PassiveSkill>

    fun addPassive(passive: Skill.PassiveSkill) {
        passives.put(passive::class, passive)
    }

    fun <K: Skill.PassiveSkill> getPassive(passive: KClass<K>): K? {
        if (passives[passive] == null)
            return null

        return passive.cast(passives[passive])
    }

    fun <K: Skill.PassiveSkill> removePassive(passive: KClass<K>) {
        passives.remove(passive)
    }
}