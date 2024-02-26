package com.neutrino.game.entities.characters

import com.neutrino.game.entities.Characters
import com.neutrino.game.entities.Entity

class Character: Entity() {

    override var name: String = ""
        get() {return if (nameSet) field else Characters.getName(id)}
        set(value) {
            nameSet = true
            field = value
        }
}