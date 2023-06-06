package com.neutrino.game.utility.serialization

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.serializers.DefaultSerializers

object KryoObj {
    val kryo: Kryo = Kryo()

    init {
        kryo.isRegistrationRequired = false
        kryo.references = true
        kryo.addDefaultSerializer(StringBuffer::class.java, DefaultSerializers.StringBufferSerializer())
        kryo.addDefaultSerializer(HeaderSerializable::class.java, HeaderFieldSerializer::class.java)

        kryo.register(TextureAtlas.AtlasRegion::class.java, AtlasRegionSerializer())
        kryo.register(com.badlogic.gdx.graphics.Color::class.java, ColorSerializer())
    }
}