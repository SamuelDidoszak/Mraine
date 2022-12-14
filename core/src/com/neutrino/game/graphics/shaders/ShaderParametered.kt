package com.neutrino.game.graphics.shaders

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram

sealed class ShaderParametered {
    abstract val shader: ShaderProgram

    open fun applyParameters() {}

    fun applyToBatch(batch: Batch?) {
        try {
            batch?.shader = shader
            applyParameters()
        } catch (e: Exception) {
//            e.printStackTrace()
            batch?.shader = null
        }
    }
}