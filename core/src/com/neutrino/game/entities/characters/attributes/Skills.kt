package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.systems.skills.Skill
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class Skills: Attribute() {
    private val skillList: ArrayList<Skill> = ArrayList()

    fun addSkill(skill: Skill) {
        skillList.add(skill)
        if (skill is Skill.PassiveSkill)
            skill.useStart()
    }
    fun addSkill(skill: KClass<out Skill>) {
        skillList.add(skill.primaryConstructor!!.call(entity))
        if (skill is Skill.PassiveSkill)
            skill.useStart()
    }
    fun removeSkill(skill: Skill) {
        skillList.remove(skill)
        if (skill is Skill.PassiveSkill)
            skill.useStop()
    }
    fun removeSkill(skill: KClass<out Skill>) {
        for (addedSkill in skillList) {
            if (addedSkill::class != skill)
                continue
            if (addedSkill is Skill.PassiveSkill)
                addedSkill.useStop()
            skillList.remove(addedSkill)
            return
        }
    }
    fun getSkills(): List<Skill> = skillList

    var maxConsecutiveSkills: Int = 3
}