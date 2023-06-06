package com.neutrino.game.utility.serialization

import com.badlogic.gdx.graphics.Color
import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.Serializer
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output

class ColorSerializer: Serializer<Color>() {

    override fun write(kryo: Kryo?, output: Output?, `object`: Color?) {
        output!!.writeInt(Color.rgba8888(`object`!!))
    }

    override fun read(kryo: Kryo?, input: Input?, type: Class<out Color>?): Color {
        val color: Color = Color()
        Color.rgba8888ToColor(color, input!!.readInt())
        return color
    }
}