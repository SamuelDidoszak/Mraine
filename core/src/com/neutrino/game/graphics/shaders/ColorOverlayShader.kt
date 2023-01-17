package com.neutrino.game.graphics.shaders

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram

class ColorOverlayShader(var color: Color): ShaderParametered() {
    override val shader: ShaderProgram = Shaders.colorOverlayShader

    companion object colors {
        val LIGHT_RED = Color(0.8f, 0.3f, 0.3f, 0.3f)
        val DARK_RED = Color(0.9f, 0.2f, 0.2f, 0.45f)
        val GREEN = Color(0.2f, 0.85f, 0.4f, 0.4f)
    }

    override fun applyToBatch(batch: Batch?) {
        try {
            batch?.color = color
            batch?.shader = shader
            applyParameters()
        } catch (e: Exception) {
//            e.printStackTrace()
            batch?.shader = null
        }
    }

    override fun cleanUp(batch: Batch?) {
        batch?.color = Color.WHITE
    }

}