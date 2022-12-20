package com.neutrino.game.graphics.utility

import com.badlogic.gdx.graphics.Color
import java.nio.ByteBuffer

@OptIn(ExperimentalUnsignedTypes::class)
class Pixel(val pixel: UByteArray) {
    fun r(): Int = pixel[0].toInt()
    fun g(): Int = pixel[1].toInt()
    fun b(): Int = pixel[2].toInt()
    fun a(): Int = pixel[3].toInt()
    fun color(): Color = Color(ByteBuffer.wrap(pixel.asByteArray()).int)
}