package com.neutrino.game.graphics.shaders

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram

object Shaders {
    val outlineShader: ShaderProgram = ShaderProgram(
        Gdx.files.internal("shaders/vertex.vert").readString(),
        Gdx.files.internal("shaders/outline.glsl").readString()
    )
}