package com.neutrino.game.graphics.shaders

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import ktx.math.div
import ktx.math.times

class OutlineShader(): ShaderParametered() {
    constructor(color: Color): this() {
        this.color = color
    }
    constructor(color: Color, thickness: Float): this() {
        this.color = color
        this.thickness = thickness
    }
    constructor(color: Color, thickness: Float, texture: TextureAtlas.AtlasRegion): this() {
        this.color = color
        this.thickness = thickness
        textureSize = Vector2(1 / texture.texture.width.toFloat(), 1 / texture.texture.height.toFloat()) / 4f
    }

    override val shader: ShaderProgram = Shaders.outlineShader
    var color: Color = Color.BLACK
    var thickness: Float = 1f
    var textureSize: Vector2 = Vector2()

    override fun applyParameters() {
        shader.setUniformf("u_outlineColor", color)
        shader.setUniformf("u_pixelSize", textureSize * thickness)
    }
}