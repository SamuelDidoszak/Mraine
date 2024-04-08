package com.neutrino.game.graphics.drawing.layers

import com.badlogic.gdx.graphics.g2d.Batch

open class LayeredGroup(
    xOffset: Float = 0f,
    yOffset: Float = 0f,
    z: Int = 0
): LayeredDraw(xOffset, yOffset, z) {

    val children: ArrayList<LayeredDraw> = ArrayList()

    override fun draw(batch: Batch, x: Float, y: Float, alpha: Float) {
        val x = x + getX()
        val y = y + getY()
        children.forEach { it.draw(batch, x, y, alpha) }
    }

    override var width: Int
        set(value) {}
        get() {
            var width = 0
            children.forEach {
                val newWidth = it.width
                if (newWidth > width)
                    width = newWidth
            }
            return width
        }

    override var height: Int
        set(value) {}
        get() {
            var height = 0
            children.forEach {
                val newHeight = it.height
                if (newHeight > height)
                    height = newHeight
            }
            return height
        }

}