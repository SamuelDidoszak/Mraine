package com.neutrino

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.util.Constants
import squidpony.squidmath.Coord
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

class GameCamera(
    val camera: Camera,
    val levelDrawer: LevelDrawer
) {

    private val startXPosition = 0f
    private val startYPosition = Constants.LevelChunkSize * 64f + 64f

    fun isPlayerFocused(): Boolean {
        return (abs(camera.position.x - Player.get(DrawPosition::class)!!.x) < 16 &&
                abs(camera.position.y - (startYPosition - Player.get(DrawPosition::class)!!.y)) < 16)
    }

    fun moveCameraToEntity(entity: Entity) {
        camera.position.lerp(Vector3(
            entity.get(DrawPosition::class)!!.x,
            entity.get(DrawPosition::class)!!.y,
            camera.position.z), 0.03f * (100f / Gdx.graphics.framesPerSecond))
    }

    fun moveCameraPosition(xPos: Int, yPos: Int) {
        camera.position.lerp(Vector3(xPos * 64f, startYPosition - yPos * 64f, camera.position.z), 0.03f)
    }

    fun setCameraPosition(x: Float, y: Float) {
        camera.position.set(x, y, camera.position.z)
    }

    fun getCameraPosition(): Pair<Int, Int> {
        val gameCamera = camera as OrthographicCamera
        val yPos = (levelDrawer.height - gameCamera.position.y) / 64
        val xPos = (gameCamera.position.x / 64)

        return Pair(xPos.roundToInt(), yPos.roundToInt())
    }

    fun isInCamera(tileX: Int, tileY: Int): Boolean {
        val gameCamera = camera as OrthographicCamera

        var yBottom = MathUtils.ceil((levelDrawer.height - (gameCamera.position.y - gameCamera.viewportHeight * gameCamera.zoom / 2f)) / 64) + 2
        var yTop = MathUtils.floor((levelDrawer.height - (gameCamera.position.y + gameCamera.viewportHeight * gameCamera.zoom / 2f)) / 64) + 1
        var xLeft: Int =
            MathUtils.floor((gameCamera.position.x - gameCamera.viewportWidth * gameCamera.zoom / 2f) / 64)
        var xRight =
            MathUtils.ceil((gameCamera.position.x + gameCamera.viewportWidth * gameCamera.zoom / 2f) / 64)

        // Make sure that values are in range
        yBottom = if (yBottom <= 0) 0 else if (yBottom > levelDrawer.map.size) levelDrawer.map.size else yBottom
        yTop = if (yTop <= 0) 0 else if (yTop > levelDrawer.map.size) levelDrawer.map.size else yTop
        xLeft = if (xLeft <= 0) 0 else if (xLeft > levelDrawer.map[0].size) levelDrawer.map[0].size else xLeft
        xRight = if (xRight <= 0) 0 else if (xRight > levelDrawer.map[0].size) levelDrawer.map[0].size else xRight

        return (tileX in xLeft..xRight) && (tileY in yTop..yBottom)
    }

    fun scroll(amount: Float) {
        if (amount.sign.toInt() == -1)
            (camera as OrthographicCamera).zoom /= 2
        else
            (camera as OrthographicCamera).zoom *= 2

        val zoom = (camera as OrthographicCamera).zoom
        if (zoom <= 0.25)
            camera.zoom = 0.25f
        else if (zoom >= 16f)
            camera.zoom = 16f
    }

    fun getTile(screenX: Int, screenY: Int): Coord {
        val touch: Vector3 = Vector3(screenX.toFloat(), screenY.toFloat(),0f)
        camera.unproject(touch)

        return getTileUnprojected(touch)
    }

    fun getTileUnprojected(position: Vector3): Coord {
        // Change the outOfBounds click behavior
        val tileX: Int = if(position.x.toInt() / 64 <= 0) 0 else
            if (position.x.toInt() / 64 >= levelDrawer.currentLevel.sizeX) levelDrawer.currentLevel.sizeX - 1 else
                position.x.toInt() / 64

        val tileY: Int = if((startYPosition - position.y) / 64 <= 0) 0 else
            if ((startYPosition - position.y) / 64 >= levelDrawer.currentLevel.sizeY) levelDrawer.currentLevel.sizeY - 1 else
                (startYPosition - position.y).toInt() / 64

        return Coord.get(tileX, tileY)
    }
}