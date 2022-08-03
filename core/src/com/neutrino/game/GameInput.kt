package com.neutrino.game

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera;

class GameInput(
    val camera: Camera
) : InputAdapter() {
    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.LEFT -> {
                camera.position.set(camera.position.x - 16, camera.position.y, 0f)
            }
            Input.Keys.RIGHT -> {
                camera.position.set(camera.position.x + 16, camera.position.y, 0f)
            }
            Input.Keys.UP -> {
                camera.position.set(camera.position.x, camera.position.y + 16, 0f)
            }
            Input.Keys.DOWN -> {
                camera.position.set(camera.position.x, camera.position.y - 16, 0f)
            }
        }
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val zoom = (camera as OrthographicCamera).zoom
        camera.position.add(-Gdx.input.deltaX.toFloat() * zoom,
            Gdx.input.deltaY.toFloat() * zoom, 0f)
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        val zoom = (camera as OrthographicCamera).zoom + (amountY / 10)
        if (zoom <= 0.1)
            (camera as OrthographicCamera).zoom = 0.1f
        else if (zoom >= 3f)
            (camera as OrthographicCamera).zoom = 3f
        else
            (camera as OrthographicCamera).zoom = zoom
        return true
    }
}