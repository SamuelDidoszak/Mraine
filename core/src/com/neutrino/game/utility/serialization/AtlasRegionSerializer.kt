package com.neutrino.game.utility.serialization

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.Serializer
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output
import com.neutrino.game.util.Constants

class AtlasRegionSerializer: Serializer<TextureAtlas.AtlasRegion>() {

    override fun write(kryo: Kryo?, output: Output?, `object`: TextureAtlas.AtlasRegion?) {
        output!!.writeInt(getTextureId(`object`!!.texture))
        output!!.writeString(`object`!!.name)
    }

    override fun read(
        kryo: Kryo?,
        input: Input?,
        type: Class<out TextureAtlas.AtlasRegion>?
    ): TextureAtlas.AtlasRegion {
        return Constants.textureArrayMap.getValueAt(input!!.readInt())!!.findRegion(input!!.readString())
    }

    private fun getTextureId(texture: Texture): Int {
        for (i in 0 until Constants.textureArrayMap.size) {
            if (Constants.textureArrayMap.getKeyAt(i) == texture)
                return i
        }
        return -1
    }
}