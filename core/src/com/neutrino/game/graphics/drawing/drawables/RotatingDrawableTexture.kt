package com.neutrino.game.graphics.drawing.drawables

import com.badlogic.gdx.graphics.g2d.Batch
import com.neutrino.game.entities.Entity
import com.neutrino.game.graphics.textures.AnimatedTextureSprite
import com.neutrino.game.graphics.textures.TextureSprite
import kotlin.math.abs
import kotlin.math.roundToInt

class RotatingDrawableTexture(
    entity: Entity,
    textureSprite: TextureSprite
): DrawableTexture(entity, textureSprite) {

    var pivotXPercent: Float = 0.5f
    var pivotYPercent: Float = 0.5f
    /** 0 - Right, 90 - Up, 180 - Left, 270 - Down */
    var rotation: Float = 0f
        set(value) {
            field = value
            if (texture.lights == null)
                return
            val rotation360 = (if (rotation > 0) rotation else 360 + rotation) % 360
            val triangle = (1 / 90f) * (90 - abs((rotation360 % 180) - 90))

            when (rotation360.toInt()) {
                in 0 .. 90 -> {
                    // width = width
                    // height = height
                } in 90 .. 180 -> {
                    // width = 1 - height
                    // height = width
                } in 180 .. 270 -> {
                    // width = 1 - width
                    // height = 1 - height
                } in 270 .. 360 -> {
                    // width = 1 - width
                    // height = height
                }
            }
        }

    private fun offsetLights(xOffset: Float, yOffset: Float) {
        if (texture.lights == null)
            return
        removeLights()
        val newLights = lights!!.copy()
        if (lights.isSingleLight)
            lights.getLight().xyDiff(xOffset, yOffset)
        else {
            if (texture is AnimatedTextureSprite) {
                for (i in 0 until lights.getLightArraySize()) {
                    for (light in lights.getLights(i)!!) {
                        light.x += xOffset
                        light.y += yOffset
                    }
                }
            } else {
                for (light in lights.getLights()!!) {
                    light.x += xOffset
                    light.y += yOffset
                }
            }
        }
        texture.lights = lights
    }

    private fun removeLights() {
    }

    private val lights: LightSources? = textureSprite.lights?.copy()

    fun setRotation(pivotXPercent: Float, pivotYPercent: Float, rotation: Float): RotatingDrawableTexture {
        this.pivotXPercent = pivotXPercent
        this.pivotYPercent = pivotYPercent
        this.rotation = rotation
        return this
    }

    override fun draw(batch: Batch, x: Float, y: Float, parentAlpha: Float) {
        batch.setAlpha(parentAlpha * alpha)

        batch.draw(texture.texture,
            if (!texture.mirrorX) x + getX() else x + getX() +
                    if (texture !is AnimatedTextureSprite) width else (texture.mirrorPivot * sizeScale).roundToInt(),
            y + getY(),
            getPivotX(),
            getPivotY(),
            width * if (!texture.mirrorX) 1f else -1f,
            height * 1f,
            1f, 1f, rotation)
    }

    private fun getPivotX(): Float {
        val percent = if (texture.mirrorX) 1f - pivotXPercent else pivotXPercent
        return width * percent
    }

    private fun getPivotY(): Float {
        val percent = if (texture.mirrorY) 1f - pivotYPercent else pivotYPercent
        return height * percent
    }

    fun getRotatedWidth(): Float {
        val triangle = (1 / 90f) * (90 - abs((rotation % 180) - 90))
        return height * triangle + (1 - triangle) * width
    }

    fun getRotatedHeight(): Float {
        val triangle = (1 / 90f) * (90 - abs((rotation % 180) - 90))
        return width * triangle + (1 - triangle) * height
    }
}