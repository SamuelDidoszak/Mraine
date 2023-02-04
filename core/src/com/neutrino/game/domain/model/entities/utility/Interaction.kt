package com.neutrino.game.domain.model.entities.utility

import com.neutrino.game.domain.model.entities.Entity
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
sealed class Interaction(var requiredDistance: Int, var isPrimary: Boolean, var actionIcon: String, var turnCost: Double = 0.0) {

    open fun act() {}

    @Serializable
    open class DOOR(@Polymorphic val entity: Entity): Interaction(1, true,"", turnCost = 1.0) {
        private var open = false
        override fun act() {
            open = !open
            entity.allowOnTop = open
            entity.allowCharacterOnTop = open
            val textureName = if (open) entity.texture.name.substringBefore("Closed") else entity.texture.name.plus("Closed")
            entity.texture = entity.getTexture(textureName)
            this.isPrimary = !open
        }
    }

    @Serializable
    class ITEM: Interaction(0, true, "", turnCost = 1.0)

    @Serializable
    class DESTROY(@Polymorphic val entity: Entity): Interaction(1, true, "", 1.0)

    @Serializable
    class OPEN: Interaction(1, true, "", 1.0)
}