package com.neutrino.game.utility.serialization

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class FBOSerializer: KSerializer<FrameBuffer> {
    override val descriptor: SerialDescriptor = FrameBufferSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: FrameBuffer) {
        val byteArray = value.colorBufferTexture.textureData.consumePixmap().pixels.array()
        val surrogate = FrameBufferSurrogate(
            byteArray,
            byteArray.size
        )
        encoder.encodeSerializableValue(FrameBufferSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): FrameBuffer {
        val surrogate = decoder.decodeSerializableValue(FrameBufferSurrogate.serializer())
        val pixmap = Pixmap(surrogate.pixelByteArray, 0, surrogate.length)

        val fbo = FrameBuffer(Pixmap.Format.RGBA8888, 100, 100, false)
        fbo.textureAttachments.set(0, Texture(pixmap))
        return fbo
    }

    @Serializable
    @SerialName("Texture")
    private class FrameBufferSurrogate(val pixelByteArray: ByteArray, val length: Int)
}