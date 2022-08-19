package com.neutrino

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.Viewport
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.map.Level

class GameStage(
    viewport: Viewport,
//    batch: SpriteBatch
): Stage(viewport) {
    var level: Level? = null
    var startXPosition: Float = 0f
    var startYPosition: Float = 800f
        set(value) {field = value + 16}
















    // Input processor

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (level == null)
            return true

        val touch: Vector3 = Vector3(screenX.toFloat(), screenY.toFloat(),0f)
        camera.unproject(touch)
        // Change the outOfBounds click behavior
        val tileX: Int = if(touch.x.toInt() / 16 <= 0) 0 else
            if (touch.x.toInt() / 16 >= level!!.map.map[0].size) level!!.map.map[0].size - 1 else
                touch.x.toInt() / 16

        val tileY: Int = if((startYPosition - touch.y) / 16 <= 0) 0 else
            if ((startYPosition - touch.y) / 16 >= level!!.map.map.size) level!!.map.map.size - 1 else
                (startYPosition - touch.y).toInt() / 16

        var entities: String = ""
        for (entity in level!!.map.map[tileY][tileX])
            entities += entity.name + ": texture= " + entity.texture.toString() + " onTop=" + entity.allowCharacterOnTop + "\n"
        println("$tileX, $tileY")

//        println("X: " + touch.x + " Y: " + touch.y)
        println(entities)
        return true
    }




    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.LEFT -> {
                Player.move(Player.xPos - 1, Player.yPos)
                camera.position.set(camera.position.x - 16, camera.position.y, 0f)
            }
            Input.Keys.RIGHT -> {
                Player.move(Player.xPos + 1, Player.yPos)
                camera.position.set(camera.position.x + 16, camera.position.y, 0f)
            }
            Input.Keys.UP -> {
                Player.move(Player.xPos, Player.yPos - 1)
                camera.position.set(camera.position.x, camera.position.y + 16, 0f)
            }
            Input.Keys.DOWN -> {
                Player.move(Player.xPos, Player.yPos + 1)
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