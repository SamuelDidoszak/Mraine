package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.Skills
import com.neutrino.game.entities.systems.skills.Skill
import kotlin.reflect.KClass

class SkillBook(
    val skill: KClass<out Skill>
): Attribute() {

    fun learn(entity: Entity) {
        if (entity hasNot Skills::class)
            entity.addAttribute(Skills())
        entity.get(Skills::class)!!.addSkill(skill)
    }
}