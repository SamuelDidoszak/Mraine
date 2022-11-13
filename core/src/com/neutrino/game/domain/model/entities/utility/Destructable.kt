package com.neutrino.game.domain.model.entities.utility

import com.neutrino.game.domain.model.items.Item

interface Destructable: Interactable {
    var entityHp: Float
    var destroyed: Boolean

    fun destroy(): MutableList<Item>? {
        destroyed = true
        if (this is Entity) {
            allowOnTop = true
            allowCharacterOnTop = true
            texture = getTexture(texture.name + "Destroyed")
        }
        if (this is Container) {
            return dropItems()
        }
        return null
    }
}