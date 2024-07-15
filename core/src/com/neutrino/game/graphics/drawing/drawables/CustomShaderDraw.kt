package com.neutrino.game.graphics.drawing.drawables

import com.badlogic.gdx.graphics.g2d.Batch

interface CustomShaderDraw {

    fun drawShader(batch: Batch, x: Float, y: Float, parentAlpha: Float)
}