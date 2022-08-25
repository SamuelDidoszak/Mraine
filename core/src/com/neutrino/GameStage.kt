package com.neutrino

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.map.Level
import squidpony.squidmath.Coord

class GameStage(
    viewport: Viewport,
//    batch: SpriteBatch
): Stage(viewport) {
    var level: Level? = null
    var startXPosition: Float = 0f
    var startYPosition: Float = 800f
        set(value) {field = value + 64}

    var waitForPlayerInput: Boolean = true
    var clickedCoordinates: Coord? = null


    fun setCameraToPlayer() {
        camera.position.x = Player.xPos * 64f
        camera.position.y = startYPosition - Player.yPos * 64f
    }


    // Input processor

    private var dragging = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button != Input.Buttons.LEFT || pointer > 0)
            return false
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        dragging = true
        val zoom = (camera as OrthographicCamera).zoom
        camera.position.add(-Gdx.input.deltaX.toFloat() * zoom,
            Gdx.input.deltaY.toFloat() * zoom, 0f)
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button != Input.Buttons.LEFT || pointer > 0) return false;
        if (level == null) return false
        if (dragging) {
            dragging = false
            return true
        }

        val touch: Vector3 = Vector3(screenX.toFloat(), screenY.toFloat(),0f)
        camera.unproject(touch)
        // Change the outOfBounds click behavior
        val tileX: Int = if(touch.x.toInt() / 64 <= 0) 0 else
            if (touch.x.toInt() / 64 >= level!!.sizeX) level!!.sizeX - 1 else
                touch.x.toInt() / 64

        val tileY: Int = if((startYPosition - touch.y) / 64 <= 0) 0 else
            if ((startYPosition - touch.y) / 64 >= level!!.sizeY) level!!.sizeY - 1 else
                (startYPosition - touch.y).toInt() / 64

        var entities: String = ""
        for (entity in level!!.map.map[tileY][tileX])
            entities += entity.name + ": texture= " + entity.texture.toString() + " onTop=" + entity.allowCharacterOnTop + "\n"

        println("clicked: $tileX, $tileY\n$entities")
        dragging = false

        if (waitForPlayerInput) {
            clickedCoordinates = Coord.get(tileX, tileY)
            waitForPlayerInput = false
        }

        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.LEFT -> {
                Player.move(Player.xPos - 1, Player.yPos)
                camera.position.set(camera.position.x - 64, camera.position.y, 0f)
            }
            Input.Keys.RIGHT -> {
                Player.move(Player.xPos + 1, Player.yPos)
                camera.position.set(camera.position.x + 64, camera.position.y, 0f)
            }
            Input.Keys.UP -> {
                Player.move(Player.xPos, Player.yPos - 1)
                camera.position.set(camera.position.x, camera.position.y + 64, 0f)
            }
            Input.Keys.DOWN -> {
                Player.move(Player.xPos, Player.yPos + 1)
                camera.position.set(camera.position.x, camera.position.y - 64, 0f)
            }
        }
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        val zoom = (camera as OrthographicCamera).zoom + (amountY / 10)
        if (zoom <= 0.4)
            (camera as OrthographicCamera).zoom = 0.4f
        else if (zoom >= 12f)
            (camera as OrthographicCamera).zoom = 12f
        else
            (camera as OrthographicCamera).zoom = zoom
        return true
    }
}