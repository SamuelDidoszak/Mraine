package com.neutrino.game.domain.model.entities.containers

import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.utility.serialization.AtlasRegion
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class Barrel: Entity(), ChangesImpassable, Destructable, Container {
    override var allowOnTop = false
    override var allowCharacterOnTop = false
    override val name = "Barrel"
    override val description = "A barrel"
    override var destroyed: Boolean = false
    override var entityHp: Float = 1f
    override val itemList: MutableList<Item> = ArrayList()
    override val itemTiers: List<Pair<Int, Float>> = listOf(Pair(1, 0.1f))

    override val textureNames: List<String> = listOf("barrel", "barrelDestroyed")
    override var texture: AtlasRegion = setTexture()
    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        texture = getTexture(textureNames[0])
    }

    override val interactionList: List<Interaction> = List(1) {
        Interaction.DESTROY(this)
    }

}