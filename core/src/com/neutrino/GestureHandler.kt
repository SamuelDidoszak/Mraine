package com.neutrino

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport

class GestureHandler(val viewport: Viewport): GestureDetector.GestureListener {
    // Android gestures
    override fun longPress(x: Float, y: Float): Boolean {
        return Gdx.input.inputProcessor.touchUp(x.toInt(), y.toInt(), 0, Input.Buttons.FORWARD)
    }

    override fun zoom(initialDistance: Float, distance: Float): Boolean {
        println("${initialDistance / distance}")
        val zoomRatio = Math.pow(initialDistance / distance.toDouble(), 1/15.0).toFloat()
        val zoom =  zoomRatio * (viewport.camera as OrthographicCamera).zoom
        if (zoom <= 0.05)
            (viewport.camera as OrthographicCamera).zoom = 0.05f
        else if (zoom >= 4f)
            (viewport.camera as OrthographicCamera).zoom = 4f
        else
            (viewport.camera as OrthographicCamera).zoom = zoom
        return true
    }

    // currently unused
    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        return false
    }

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        return false
    }

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
        return false
    }

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun pinch(
        initialPointer1: Vector2?,
        initialPointer2: Vector2?,
        pointer1: Vector2?,
        pointer2: Vector2?
    ): Boolean {
        return true
    }

    override fun pinchStop() {
    }
}