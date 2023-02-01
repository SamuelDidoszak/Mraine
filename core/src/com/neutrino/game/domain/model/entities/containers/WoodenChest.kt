package com.neutrino.game.domain.model.entities.containers

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.items.Item
import java.util.*
import kotlin.random.Random

class WoodenChest: Entity(), ChangesImpassable, Container, Interactable {
    override val name: String = "Wooden chest"
    override var allowOnTop: Boolean = false
    override var allowCharacterOnTop: Boolean = false

    override val textureNames: List<String> = listOf("woodenChest")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override val itemList: MutableList<Item> = ArrayList()
    override val itemTiers: List<Pair<Int, Float>> = listOf(
        Pair(1, 1f),
        Pair(2, 1f),
        Pair(3, 1f)
    )
    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        texture = getTexture(textureNames[0])
    }

    override val interactionList: List<Interaction> = List(1) {
        Interaction.OPEN()
    }


}