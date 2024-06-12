package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.entities.systems.skills.Skill

interface HasSkills {
    val skillList: ArrayList<Skill>
    /** Determines the maximum number of concurrently used skills that do not use mana */
    var maxSkills: Int
}