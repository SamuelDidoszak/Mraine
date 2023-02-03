package com.neutrino.game.domain.model.entities.utility

import com.neutrino.LevelArrays
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.systems.attack.utility.AttackData
import com.neutrino.game.domain.model.systems.attack.utility.AttackableRequiresCoord
import com.neutrino.game.lessThanDelta
import squidpony.squidmath.Coord

interface Destructable: Interactable, AttackableRequiresCoord {
    var entityHp: Float
    var destroyed: Boolean

    override fun getDamage(data: AttackData, coord: Coord) {
        entityHp -= data.getDamageSum()
        if (entityHp.lessThanDelta(0f)) {
            destroy(coord)
        }
    }

    fun destroy(coord: Coord) {
        val items = destroy()
        if (items != null) {
            for (item in items) {
                LevelArrays.getEntitiesAt(coord).add(ItemEntity(item))
            }
        }
        LevelArrays.getImpassableList().remove(coord)
    }

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