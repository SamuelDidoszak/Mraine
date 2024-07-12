package com.neutrino.game.graphics.shaders

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.entities.util.Equality
import com.neutrino.game.graphics.drawing.drawables.DrawableTexture
import com.neutrino.game.graphics.textures.AnimatedTextureSprite
import ktx.math.div
import ktx.math.times

class OutlineShader(): ShaderParametered(), Cloneable<OutlineShader>, Equality<OutlineShader> {
    constructor(color: Color, thickness: Float): this() {
        this.color = color
        this.thickness = thickness
    }

    fun setTexture(drawableTexture: DrawableTexture) {
        if (drawableTexture.texture is AnimatedTextureSprite)
            animatedTexture = drawableTexture
        val texture = drawableTexture.texture.texture
        textureSize = Vector2(1 / texture.texture.width.toFloat(), 1 / texture.texture.height.toFloat()) / 4f
        boundaries[0] = texture.u
        boundaries[1] = texture.v
        boundaries[2] = texture.u2
        boundaries[3] = texture.v2
    }

    override val shader: ShaderProgram = ShaderPrograms.outlineShader
    var color: Color = Color.BLACK
    var thickness: Float = 1f
    var textureSize: Vector2 = Vector2()
    var boundaries: FloatArray = FloatArray(4)
    private var animatedTexture: DrawableTexture? = null

    override fun applyParameters() {
        if (animatedTexture != null)
            setTexture(animatedTexture!!)
        shader.setUniformf("u_outlineColor", color)
        shader.setUniformf("u_pixelSize", textureSize * thickness)
        shader.setUniformf("u_texBoundaries", boundaries[0], boundaries[1], boundaries[2], boundaries[3])
    }

    companion object {
        val OUTLINE_GREEN = Color.FOREST
        val OUTLINE_RED = Color.FIREBRICK
        val OUTLINE_BLACK = Color.BLACK
        val OUTLINE_CLEAR = Color(0f, 0f, 0f, 0f)
    }

    override fun cleanUp(batch: Batch?) {
        batch?.shader = null
    }

    override fun clone(): OutlineShader {
        return OutlineShader(color, thickness)
    }

    override fun isEqual(other: OutlineShader): Boolean = color == other.color && thickness == other.thickness
}