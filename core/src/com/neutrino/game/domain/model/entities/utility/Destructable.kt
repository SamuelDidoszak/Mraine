package com.neutrino.game.domain.model.entities.utility

import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.systems.attack.utility.AttackData
import com.neutrino.game.domain.model.systems.attack.utility.Attackable
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.lessThanDelta
import squidpony.squidmath.Coord

interface Destructable: Interactable, Attackable {
    var entityHp: Float
    var destroyed: Boolean

    override fun getDamage(data: AttackData) {
        entityHp -= data.getDamageSum()
        if (entityHp.lessThanDelta(0f)) {
            val items = destroy()
            if (items != null) {
                for (item in items) {
                    Turn.currentLevel.map.map[Player.ai.entityTargetCoords!!.second][Player.ai.entityTargetCoords!!.first].add(ItemEntity(item))
                }
            }
            Turn.mapImpassableList.remove(Coord.get(Player.ai.entityTargetCoords!!.first, Player.ai.entityTargetCoords!!.second))
        }
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