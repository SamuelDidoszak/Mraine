package com.neutrino.game.graphics.shaders

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import ktx.math.div
import ktx.math.times

class OutlineShader(): ShaderParametered() {
    constructor(color: Color, thickness: Float, texture: TextureAtlas.AtlasRegion): this() {
        this.color = color
        this.thickness = thickness
        textureSize = Vector2(1 / texture.texture.width.toFloat(), 1 / texture.texture.height.toFloat()) / 4f
        setBoundaries(texture)
    }

    fun setBoundaries(texture: TextureAtlas.AtlasRegion) {
        boundaries[0] = texture.u
        boundaries[1] = texture.v
        boundaries[2] = texture.u2
        boundaries[3] = texture.v2
    }

    override val shader: ShaderProgram = Shaders.outlineShader
    var color: Color = Color.BLACK
    var thickness: Float = 1f
    var textureSize: Vector2 = Vector2()
    var boundaries: FloatArray = FloatArray(4)

    override fun applyParameters() {
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
}