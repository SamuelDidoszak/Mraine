package com.neutrino.game.entities.map.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.entities.shared.util.InteractionType

class Destructable(var entityHp: Float): Attribute() {
    var destroyed: Boolean = false

    init {
        if (!(entity has Interaction::class))
            entity addAttribute Interaction(arrayListOf())
        val interaction = InteractionType.DESTROY()
        interaction.entity = entity
        entity.get(Interaction::class)!!.interactionList.add(interaction)
    }

//    fun getDamage(data: AttackData, coord: Coord) {
//        entityHp -= data.getDamageSum()
//        if (entityHp.lessThanDelta(0f)) {
//            destroy(coord)
//        }
//    }
//
//    fun destroy(coord: Coord) {
//        val items = destroy()
//        if (items != null) {
//            for (item in items) {
//                LevelArrays.getEntitiesAt(coord).add(ItemEntity(item))
//            }
//        }
//        LevelArrays.getImpassableList().remove(coord)
//    }
//
//    fun destroy(): MutableList<Item>? {
//        destroyed = true
//        if (this is Entity) {
//            allowOnTop = true
//            allowCharacterOnTop = true
//            texture = getTexture(texture.name + "Destroyed")
//        }
//        if (this is Container) {
//            return dropItems()
//        }
//        return null
//    }
}