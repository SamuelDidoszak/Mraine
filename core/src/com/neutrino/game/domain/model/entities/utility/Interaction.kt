package com.neutrino.game.domain.model.entities.utility

sealed class Interaction(var requiredDistance: Int, var isPrimary: Boolean, var actionIcon: String, var turnCost: Double = 0.0) {
    open fun act() {}
    open class DOOR(val entity: Entity): Interaction(1, true,"", turnCost = 1.0) {
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

    class ITEM: Interaction(0, true, "", turnCost = 1.0)

    class DESTROY(val entity: Entity): Interaction(1, true, "", 1.0)

    class OPEN: Interaction(1, true, "", 1.0)
}