package com.neutrino.game.utility.serialization

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object AtlasRegionSerializer: KSerializer<TextureAtlas.AtlasRegion> {
    override val descriptor: SerialDescriptor = AtlasRegionSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: TextureAtlas.AtlasRegion) {
        val surrogate = AtlasRegionSurrogate(value.name, value.texture.hashCode())
        encoder.encodeSerializableValue(AtlasRegionSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): TextureAtlas.AtlasRegion {
        val surrogate = decoder.decodeSerializableValue(AtlasRegionSurrogate.serializer())
        return Constants.textureHashCodes[surrogate.textureSource]!!.findRegion(surrogate.regionName)
    }

    @Serializable
    @SerialName("AtlasRegion")
    private class AtlasRegionSurrogate(
        val regionName: String,
        val textureSource: Int
    )
}

typealias AtlasRegion = @Serializable(AtlasRegionSerializer::class) TextureAtlas.AtlasRegion