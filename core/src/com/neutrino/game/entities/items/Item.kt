package com.neutrino.game.entities.items

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.Items
import com.neutrino.game.entities.util.AttributeOperations

class Item: Entity() {

    override var name: String = ""
        get() {return if (nameSet) field else Items.getName(id)}
        set(value) {
            nameSet = true
            field = value
        }

    fun getItemAttributes(): List<Attribute> {
        val attributes = ArrayList<Attribute>()
        for (attribute in this.attributes.values) {
            if (attribute is AttributeOperations<*>)
                attributes.add(attribute)
        }
        return attributes.toList()
    }
}