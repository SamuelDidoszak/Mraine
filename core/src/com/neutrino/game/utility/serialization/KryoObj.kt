package com.neutrino.game.utility.serialization

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.objenesis.strategy.SerializingInstantiatorStrategy
import com.esotericsoftware.kryo.kryo5.serializers.CompatibleFieldSerializer
import com.esotericsoftware.kryo.kryo5.serializers.DefaultSerializers
import com.neutrino.game.domain.model.map.Level
import de.javakaffee.kryoserializers.KryoReflectionFactorySupport

object Kryo {
    val kryo: Kryo = Kryo()
    val kryoReflective = KryoReflectionFactorySupport()

    init {
        kryoReflective.isRegistrationRequired = false
        kryoReflective.references = true

        kryo.isRegistrationRequired = false
        kryo.references = true
        kryo.instantiatorStrategy = SerializingInstantiatorStrategy()
        kryo.addDefaultSerializer(StringBuffer::class.java, DefaultSerializers.StringBufferSerializer())

        val levelSerializer = CompatibleFieldSerializer<Level>(kryo, Level::class.java)
        levelSerializer.compatibleFieldSerializerConfig.copyTransient = false
        levelSerializer.compatibleFieldSerializerConfig.serializeTransient = false
        kryo.register(Level::class.java, levelSerializer)

        kryo.register(TextureAtlas.AtlasRegion::class.java, AtlasRegionSerializer())
        kryo.register(Color::class.java, ColorSerializer())
//        kryo.register(Entity::class.java, EntitySerialization())
    }
}