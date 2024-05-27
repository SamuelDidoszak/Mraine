package com.neutrino.game.entities.items

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.Items
import com.neutrino.game.entities.items.attributes.Amount
import com.neutrino.game.entities.map_entities.attributes.PickUp
import com.neutrino.game.entities.shared.attributes.Shaders
import com.neutrino.game.entities.util.AttributeOperations
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.graphics.shaders.OutlineShader

class Item: Entity(), Cloneable<Item> {

    override var name: String = ""
        get() {return if (nameSet) field else Items.getName(id)}
        set(value) {
            nameSet = true
            field = value
        }

    init {
        addAttribute(PickUp())
        addAttribute(Shaders(OutlineShader(OutlineShader.OUTLINE_BLACK, 2f)))
    }

    fun getItemAttributes(): List<Attribute> {
        val attributes = ArrayList<Attribute>()
        for (attribute in this.attributes.values) {
            if (attribute is AttributeOperations<*>)
                attributes.add(attribute)
        }
        return attributes.toList()
    }

    override fun clone(): Item {
        val item = Item()
        item.id = id
        if (nameSet)
            item.name = name
        for (attribute in attributes) {
            if (attribute.value is Cloneable<*>)
                item.addAttribute((attribute.value as Cloneable<Attribute>).clone())
        }
        return item
    }

    fun setAmount(amount: Int): Entity {
        if (amount <= get(Amount::class)!!.maxStack)
            get(Amount::class)!!.amount = amount
        return this
    }
}