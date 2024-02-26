package com.neutrino.game.entities.items

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.Items

class Item: Entity() {

    override var name: String = ""
        get() {return if (nameSet) field else Items.getName(id)}
        set(value) {
            nameSet = true
            field = value
        }
}