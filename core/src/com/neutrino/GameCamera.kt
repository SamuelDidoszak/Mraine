package com.neutrino

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.util.Constants
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

class GameCamera(
    val camera: Camera,
    private val stage: GameStage
) {

    private val startYPosition = Constants.ChunkSize * 64f + 64f

    private val levelDrawer
        get() = stage.actors[0] as LevelDrawer

    fun isPlayerFocused(): Boolean {
        val playerChunk = ChunkManager.getDrawer(Player.get(Position::class)!!.chunk)
        return (abs(camera.position.x - playerChunk.x - Player.get(DrawPosition::class)!!.x) < 16 &&
                abs(camera.position.y - playerChunk.y - Player.get(DrawPosition::class)!!.y) < 16)
    }

    fun moveCameraToEntity(entity: Entity) {
        val alpha = (0.03f * 60f * Gdx.graphics.deltaTime).coerceIn(0f, 1f)
        val entityChunk = ChunkManager.getDrawer(entity.get(Position::class)!!.chunk)
        camera.position.lerp(Vector3(
            entityChunk.x + entity.get(DrawPosition::class)!!.x,
            entityChunk.y + entity.get(DrawPosition::class)!!.y,
            camera.position.z),
            alpha)
    }

    fun moveCameraPosition(xPos: Int, yPos: Int) {
        val alpha = (0.03f * 60f * Gdx.graphics.deltaTime).coerceIn(0f, 1f)
        camera.position.lerp(Vector3(xPos * 64f, startYPosition - yPos * 64f, camera.position.z), alpha)
    }

    fun setCameraToEntity(entity: Entity) {
        val entityChunk = ChunkManager.getDrawer(entity.get(Position::class)!!.chunk)
        camera.position.set(
            entityChunk.x + entity.get(DrawPosition::class)!!.x,
            entityChunk.y + entity.get(DrawPosition::class)!!.y,
            camera.position.z)
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

    fun getTile(screenX: Int, screenY: Int): Position {
        val touch = Vector3(screenX.toFloat(), screenY.toFloat(),0f)
        camera.unproject(touch)

        return getTileUnprojected(touch)
    }

    fun getTileUnprojected(position: Vector3): Position {
        var tileX: Int = position.x.toInt() / 64
        var tileY: Int = (startYPosition - position.y).toInt() / 64

        if (position.x < 0)
            tileX -= 1
        if (startYPosition - position.y < 0)
            tileY -= 1

        val tile = ChunkManager.getCorrectPosition(Position(tileX, tileY, ChunkManager.middleChunk.chunkCoords))
        return tile
    }
}