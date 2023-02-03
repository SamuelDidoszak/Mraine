package com.neutrino.game.utility.serialization

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ColorSerializer: KSerializer<Color> {

    override val descriptor: SerialDescriptor = ColorSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Color) {
        val surrogate = ColorSurrogate(value.r, value.g, value.b, value.a)
        encoder.encodeSerializableValue(ColorSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Color {
        val surrogate = decoder.decodeSerializableValue(ColorSurrogate.serializer())
        return Color(surrogate.r, surrogate.g, surrogate.b, surrogate.a)
    }

    @Serializable
    @SerialName("Color")
    private class ColorSurrogate(val r: Float, val g: Float, val b: Float, val a: Float)
}