package com.neutrino.game.graphics.utility

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas

@OptIn(ExperimentalUnsignedTypes::class)
class PixelData(texture: Texture) {
    constructor(texture: TextureAtlas): this(texture.textures.first())

    private val pixelArray = Array(texture.height) { Array(texture.width) { UByteArray(4) } }

    init {
        texture.textureData.prepare()
        val buffer = texture.textureData.consumePixmap().pixels
        val width = texture.width * 4
        for (y in 0 until texture.height) {
            for (x in 0 until texture.width) {
                for (z in 0 until 4) {
                    pixelArray[y][x][z] = buffer[y * width + x * 4 + z].toUByte()
                }
            }
        }
    }

    fun getPixel(x: Int, y: Int): Pixel {
        return Pixel(pixelArray[y][x])
    }
}