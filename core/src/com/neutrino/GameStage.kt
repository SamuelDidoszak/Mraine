package com.neutrino

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.graphics.shaders.OutlineShader
import com.neutrino.game.graphics.utility.EntityLookupPopup
import com.neutrino.game.graphics.utility.ItemDetailsPopup
import squidpony.squidmath.Coord
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

class GameStage(
    viewport: Viewport
): Stage(viewport,
    SpriteBatch(1000,
        ShaderProgram(
            Gdx.files.internal("shaders/vertex.vert").readString(),
            Gdx.files.internal("shaders/fragmentAlphas.frag").readString()
        ))) {
    init {
        root = GameStageGroup()
        root.name = "GameStage"
    }
    var level: Level? = null
    var startXPosition: Float = 0f
    var startYPosition: Float = 800f
        set(value) {field = value + 64}

    var waitForPlayerInput: Boolean = true
    var clickedCoordinates: Coord? = null
    var focusPlayer: Boolean = false
    var lookingAround: Boolean = false

    var showEq: Boolean = false

    val animatedArray: ArrayList<Animated> = ArrayList()

    private var stateTime: Float = 0f

    fun animateAll() {
        stateTime += Gdx.graphics.deltaTime
        for (animated in animatedArray)
            animated.setFrame(stateTime)
    }

    fun isPlayerFocused(): Boolean {
        return (abs(camera.position.x - Player.xPos * 64f) < 16 &&
            abs(camera.position.y - (startYPosition - Player.yPos * 64)) < 16)
    }

    fun setCameraToPlayer() {
        camera.position.lerp(Vector3(Player.xPos * 64f, startYPosition - Player.yPos * 64f, camera.position.z), 0.03f * (100f / Gdx.graphics.framesPerSecond))
    }

    fun setCameraPosition(xPos: Int, yPos: Int) {
        camera.position.lerp(Vector3(xPos * 64f, startYPosition - yPos * 64f, camera.position.z), 0.03f)
    }

    fun getCameraPosition(): Pair<Int, Int> {
        val gameCamera = camera as OrthographicCamera
        val yPos = (level!!.height - gameCamera.position.y) / 64
        val xPos = (gameCamera.position.x / 64)

        return Pair(xPos.roundToInt(), yPos.roundToInt())
    }

    fun isInCamera(tileX: Int, tileY: Int): Boolean {
        val gameCamera = camera as OrthographicCamera

        var yBottom = MathUtils.ceil((level!!.height - (gameCamera.position.y - gameCamera.viewportHeight * gameCamera.zoom / 2f)) / 64) + 2
        var yTop = MathUtils.floor((level!!.height - (gameCamera.position.y + gameCamera.viewportHeight * gameCamera.zoom / 2f)) / 64) + 1
        var xLeft: Int =
            MathUtils.floor((gameCamera.position.x - gameCamera.viewportWidth * gameCamera.zoom / 2f) / 64)
        var xRight =
            MathUtils.ceil((gameCamera.position.x + gameCamera.viewportWidth * gameCamera.zoom / 2f) / 64)

        // Make sure that values are in range
        yBottom = if (yBottom <= 0) 0 else if (yBottom > level!!.map.map.size) level!!.map.map.size else yBottom
        yTop = if (yTop <= 0) 0 else if (yTop > level!!.map.map.size) level!!.map.map.size else yTop
        xLeft = if (xLeft <= 0) 0 else if (xLeft > level!!.map.map[0].size) level!!.map.map[0].size else xLeft
        xRight = if (xRight <= 0) 0 else if (xRight > level!!.map.map[0].size) level!!.map.map[0].size else xRight

        return (tileX in xLeft..xRight) && (tileY in yTop..yBottom)
    }


    // Input processor

    private var dragging = false
    private var touchDownCoords: Pair<Int, Int> = Pair(0, 0)
    private var calledFromLongpress: Boolean = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!(button != Input.Buttons.LEFT || button != Input.Buttons.RIGHT) || pointer > 0)
            return false
        touchDownCoords = Pair(screenX, screenY)
        if (calledFromLongpress) calledFromLongpress = false
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (max(abs(touchDownCoords.first - screenX), abs(touchDownCoords.second - screenY)) < 32)
            return true
        dragging = true
        lookingAround = true
        val zoom = (camera as OrthographicCamera).zoom
        camera.position.add(-Gdx.input.deltaX.toFloat() * zoom,
            Gdx.input.deltaY.toFloat() * zoom, 0f)
        return true
    }


    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!(button != Input.Buttons.LEFT || button != Input.Buttons.RIGHT || button != Input.Buttons.FORWARD) || pointer > 0) return false
        if (level == null) return false
        if (dragging) {
            dragging = false
            return true
        }
        // related to android long press handling
        if (calledFromLongpress)
            return true
        if (button == Input.Buttons.FORWARD)
            calledFromLongpress = true
        val button = if (button == Input.Buttons.FORWARD) Input.Buttons.RIGHT else button

        // If there is a popup, remove it
        var currPopup: Table? = this.actors.find { it.name == "entityPopup" } as EntityLookupPopup?
        if (currPopup == null)
            currPopup = this.actors.find { it.name == "itemDetails" } as ItemDetailsPopup?
        if (currPopup != null) {
            currPopup.remove()

            if (button == Input.Buttons.RIGHT || button == Input.Buttons.FORWARD)
                return true
        }

        val touch: Vector3 = Vector3(screenX.toFloat(), screenY.toFloat(),0f)
        camera.unproject(touch)
        val tile = getTileUnprojected(touch)

        // create the entityLookupPopup
        if (button == Input.Buttons.RIGHT) {
            val popup: Table
            if (level!!.getTopItem(tile.x, tile.y) != null) {
                popup = ItemDetailsPopup(level!!.getTopItem(tile.x, tile.y)!!, false)
                popup.width = 300f
                popup.assignBg(touch.x, touch.y)
            }
            else {
                popup = EntityLookupPopup(level!!.map.map[tile.y][tile.x], level!!.characterMap[tile.y][tile.x])
                popup.assignBg(touch.x, touch.y)
            }
            this.addActor(popup)
            popup.setPosition(touch.x, touch.y)
            return true
        }

        dragging = false

        if (waitForPlayerInput) {
            clickedCoordinates = Coord.get(tile.x, tile.y)
            waitForPlayerInput = false
        }

        focusPlayer = false

        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.TAB -> {
                showEq = true
            }
            Input.Keys.NUM_2 -> {
                if (level != null)
                    level!!.drawFovFow += 1
            }
            Input.Keys.LEFT -> {
//                Player.move(Player.xPos - 1, Player.yPos)
                camera.position.set(camera.position.x - 64, camera.position.y, 0f)
            }
            Input.Keys.RIGHT -> {
//                Player.move(Player.xPos + 1, Player.yPos)
                camera.position.set(camera.position.x + 64, camera.position.y, 0f)
            }
            Input.Keys.UP -> {
//                Player.move(Player.xPos, Player.yPos - 1)
                camera.position.set(camera.position.x, camera.position.y + 64, 0f)
            }
            Input.Keys.DOWN -> {
//                Player.move(Player.xPos, Player.yPos + 1)
                camera.position.set(camera.position.x, camera.position.y - 64, 0f)
            }
        }
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        if (amountY.sign.toInt() == -1)
            (camera as OrthographicCamera).zoom /= 2
        else
            (camera as OrthographicCamera).zoom *= 2
        return true

//        var zoom = (camera as OrthographicCamera).zoom
//        zoom += amountY * zoom / 10
//        if (zoom <= 0.4)
//            (camera as OrthographicCamera).zoom = 0.4f
//        else if (zoom >= 12f)
//            (camera as OrthographicCamera).zoom = 12f
//        else
//            (camera as OrthographicCamera).zoom = zoom
//        return true
    }

    private var outlinedEntity: Entity? = null
    private var outlinedCharacter: Character? = null
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val coord = getTile(screenX, screenY)
        if (level?.discoveredMap?.get(coord.y)?.get(coord.x) == true) {
            addCharacterOutline(coord.x, coord.y)
            addOutline(coord.x, coord.y)
        }

        return super.mouseMoved(screenX, screenY)
    }

    fun addOutline(xPos: Int, yPos: Int): Boolean {
        val entity = level?.getEntityWithAction(xPos, yPos)
        if (entity == outlinedEntity || entity is ItemEntity)
            return false

        outlinedEntity?.shaders?.removeAll { it is OutlineShader }
        outlinedEntity = entity
        outlinedEntity?.shaders?.add(
            OutlineShader(
            if ((outlinedEntity as Interactable).getPrimaryInteraction() is Interaction.DESTROY) {
                if ((outlinedEntity as Destructable).destroyed)
                    OutlineShader.OUTLINE_CLEAR
                else
                    OutlineShader.OUTLINE_RED
            }
            else
                OutlineShader.OUTLINE_GREEN,
            2f,
            outlinedEntity!!.texture
            )
        )
        return outlinedEntity != null
    }

    fun addCharacterOutline(xPos: Int, yPos: Int): Boolean {
        val character: Character? = level!!.characterMap[yPos][xPos]
        return addCharacterOutline(character)
    }

    fun addCharacterOutline(character: Character?): Boolean {
        if (character == outlinedCharacter || character == Player)
            return false

        outlinedCharacter?.shaders?.removeAll { it is OutlineShader }
        outlinedCharacter = character
        if (character?.isAlive() != true)
            return false

        outlinedCharacter?.shaders?.add(
            OutlineShader(
                OutlineShader.OUTLINE_RED,
                2f,
                outlinedCharacter!!.texture
            )
        )
        return outlinedCharacter != null
    }

    fun getTile(screenX: Int, screenY: Int): Coord {
        val touch: Vector3 = Vector3(screenX.toFloat(), screenY.toFloat(),0f)
        camera.unproject(touch)

        return getTileUnprojected(touch)
    }


    fun getTileUnprojected(position: Vector3): Coord {
        // Change the outOfBounds click behavior
        val tileX: Int = if(position.x.toInt() / 64 <= 0) 0 else
            if (position.x.toInt() / 64 >= level!!.sizeX) level!!.sizeX - 1 else
                position.x.toInt() / 64

        val tileY: Int = if((startYPosition - position.y) / 64 <= 0) 0 else
            if ((startYPosition - position.y) / 64 >= level!!.sizeY) level!!.sizeY - 1 else
                (startYPosition - position.y).toInt() / 64

        return Coord.get(tileX, tileY)
    }

    override fun getRoot(): Group {
        return super.getRoot()
    }

    override fun addActor(actor: Actor?) {
        super.addActor(actor)
        if (actor is Animated)
            animatedArray.add(actor)
    }


    /**
     * Group functioning as a root of GameStage
     */
    inner class GameStageGroup: Group() {
        override fun removeActor(actor: Actor?, unfocus: Boolean): Boolean {
            if (actor is Animated)
                animatedArray.remove(actor)
            return super.removeActor(actor, unfocus)
        }
    }
}