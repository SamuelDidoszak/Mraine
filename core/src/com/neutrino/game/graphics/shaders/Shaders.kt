package com.neutrino.game.graphics.shaders

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram

object Shaders {
    val fragmentAlphas: ShaderProgram = ShaderProgram(
        Gdx.files.internal("shaders/vertex.vert").readString(),
        Gdx.files.internal("shaders/fragmentAlphas.frag").readString()
    )
    val defaultShader: ShaderProgram = ShaderProgram(
        Gdx.files.internal("shaders/vertex.vert").readString(),
        Gdx.files.internal("shaders/fragmentDefault.frag").readString()
    )
    val outlineShader: ShaderProgram = ShaderProgram(
        Gdx.files.internal("shaders/vertex.vert").readString(),
        Gdx.files.internal("shaders/outline.frag").readString()
    )
    val lightShader: ShaderProgram = ShaderProgram(
        Gdx.files.internal("shaders/vertex.vert").readString(),
        Gdx.files.internal("shaders/light.frag").readString()
    )
    val blurShader: ShaderProgram = ShaderProgram(
        Gdx.files.internal("shaders/vertex.vert").readString(),
        Gdx.files.internal("shaders/blur.frag").readString()
    )
    val colorOverlayShader: ShaderProgram = ShaderProgram(
        Gdx.files.internal("shaders/vertex.vert").readString(),
        Gdx.files.internal("shaders/colorOverlay.frag").readString()
    )
}