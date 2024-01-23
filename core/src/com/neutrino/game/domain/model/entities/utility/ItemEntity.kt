package com.neutrino.game.domain.model.entities.utility

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.entities.shared.util.InteractionType
import com.neutrino.game.graphics.shaders.OutlineShader
import com.neutrino.game.graphics.shaders.ShaderParametered
import com.neutrino.game.utility.serialization.HeaderSerializable

import kotlin.random.Random


class ItemEntity(@Transient val item: Item): Entity(), Interactable, HeaderSerializable {

    override fun serializeHeader(kryo: Kryo?, output: Output?) {
        kryo!!.writeClassAndObject(output, item)
    }

    constructor(kryo: Kryo?, input: Input?): this(kryo!!.readClassAndObject(input!!) as Item)

    @Transient
    override val name: String = item.name
    @Transient
    override var allowOnTop: Boolean = true
    @Transient
    override var allowCharacterOnTop: Boolean = true

    @Transient
    override val textureNames: List<String> = item.textureNames
    @Transient
    override var texture: TextureAtlas.AtlasRegion = item.texture
    // Unnecessarily required for entity

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) { }

    @Transient
    override val interactionList: List<InteractionType> = List(1) {
        InteractionType.ITEM()
    }

    @Transient
    override var shaders: ArrayList<ShaderParametered?> = arrayListOf(OutlineShader(OutlineShader.OUTLINE_BLACK, 2f, texture))
}