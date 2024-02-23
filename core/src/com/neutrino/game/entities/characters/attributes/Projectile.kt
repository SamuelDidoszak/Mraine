package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position

class Projectile: Attribute() {

    fun shoot(entity: Entity) {
        shoot(entity.get(Position::class)!!)
    }

    fun shoot(position: Position) {

    }
}