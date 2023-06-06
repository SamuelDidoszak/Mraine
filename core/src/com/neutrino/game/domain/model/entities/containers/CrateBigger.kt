package com.neutrino.game.domain.model.entities.containers

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.items.Item

import kotlin.random.Random

class CrateBigger: Entity(), ChangesImpassable, Destructable, Container {
    @Transient
    override val name = "CrateBigger"
    override var allowOnTop = false
    override var allowCharacterOnTop = false
    @Transient
    override val description = "A bigger crate. Surprisingly, it has more volume than a smaller one"
    override var destroyed: Boolean = false
    override var entityHp: Float = 1f
    override val itemList: MutableList<Item> = ArrayList()
    @Transient
    override val itemTiers: List<Pair<Int, Float>> = listOf(
        Pair(1, 0.35f),
        Pair(2, 0.1f)
    )

    @Transient
    override val textureNames: List<String> = listOf("crateBiggerDark")
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        texture = getTexture(textureNames[0])
        mirror(20f, randomGenerator)
    }

    override val interactionList: List<Interaction> = List(1) {
        Interaction.DESTROY(this)
    }
}