package com.neutrino.game.domain.model.systems.event.types

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.HasSkills
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.Event
import com.neutrino.game.domain.model.systems.skills.Skill
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class EventLearnSkill(): Event() {
    constructor(character: Character, skill: KClass<out Skill>) : this() {
        this.character = character
        this.skill = skill
    }

    constructor(skill: KClass<out Skill>) : this() {
        this.skill = skill
    }

    override val data: MutableMap<String, Data<*>> = mutableMapOf(
        Pair("character", Data<Character>()),
        Pair("skill", Data<KClass<out Skill>>())
    )

    var character: Character
        get() { return get("character", Character::class)!! }
        set(value) { set("character", value) }

    var skill: KClass<out Skill>
        get() { return get("skill", Skill::class::class)!! }
        set(value) { set("skill", value) }

    override fun start() {
        if (!checkData())
            return

        if (character !is HasSkills)
            return

        (character as HasSkills).skillList.add(skill.primaryConstructor!!.call(character))
    }

    override fun toString(): String {
        return "Learn ${skill.simpleName?.replace("Skill", "")} skill"
    }
}