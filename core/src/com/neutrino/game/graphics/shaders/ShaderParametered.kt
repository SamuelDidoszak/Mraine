package com.neutrino.game.graphics.shaders

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram


/**
 * If shaderParametered uses different values per texture,
 * for example it uses its texture coordinates,
 * implement Cloneable and Equality interfaces
 */
sealed class ShaderParametered {
    abstract val shader: ShaderProgram

    /**
     * Applies required shader parameters
     */
    open fun applyParameters() {}

    /**
     * Applies the shader to the batch
     */
    open fun applyToBatch(batch: Batch?) {
        try {
            batch?.shader = shader
            applyParameters()
        } catch (e: Exception) {
//            e.printStackTrace()
            batch?.shader = null
        }
    }

    /**
     * Reverts all the changes made to batch
     */
    open fun cleanUp(batch: Batch?) {

    }
}