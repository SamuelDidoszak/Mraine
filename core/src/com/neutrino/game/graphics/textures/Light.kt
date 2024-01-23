package com.neutrino.game.graphics.textures

import com.badlogic.gdx.graphics.Color

data class Light(
    var x: Float,
    var y: Float,
    val color: Color,
    val intensity: Float = color.a * 255f % 25f,
    val radius: Float = setRadius(intensity)
) {

    override fun toString(): String {
        fun format(num: Float): String {
            val i = num.toInt()
            return if (num.compareTo(i) == 0) i.toString() else num.toString()
        }
        return "Light(${format(x)}f, ${format(y)}f, Color.valueOf(\"$color\"), ${format(intensity)}f, ${format(radius)}f)"
    }

    fun xyDiff(x: Float, y: Float): Light {
        this.x += x
        this.y += y
        return this
    }

    companion object {
        private fun setRadius(intensity: Float): Float {
            return when (intensity.toInt()) {
                0 -> 8f
                1 -> 16f
                2 -> 24f
                3 -> 32f
                4 -> 48f
                5 -> 64f
                6 -> 128f
                7 -> 192f
                8 -> 256f
                9 -> 320f
                10 -> 384f
                11 -> 448f
                12 -> 512f
                13 -> 640f
                14 -> 768f
                15 -> 896f
                16 -> 1024f
                17 -> 1280f
                18 -> 1536f
                19 -> 1792f
                20 -> 2048f
                21 -> 2560f
                22 -> 3072f
                23 -> 3584f
                24 -> 4096f
                else -> 32f
            }
        }
    }
}