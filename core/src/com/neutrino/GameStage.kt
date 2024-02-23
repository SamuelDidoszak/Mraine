package com.neutrino

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.neutrino.game.UI.popups.EntityLookupPopup
import com.neutrino.game.UI.popups.ItemDetailsPopup
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.graphics.shaders.Shaders
import com.neutrino.game.utility.Highlighting
import squidpony.squidmath.Coord
import java.lang.Integer.max
import kotlin.math.abs

class GameStage(
    viewport: Viewport
): Stage(viewport,
    SpriteBatch(1000, Shaders.fragmentAlphas)) {

    val gameCamera = GameCamera(camera, this)

    var waitForPlayerInput: Boolean = true
    var clickedCoordinates: Coord? = null
    var focusPlayer: Boolean = false
    var lookingAround: Boolean = false

    var showEq: Boolean = false

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
        val tile = gameCamera.getTileUnprojected(touch)

        // create the entityLookupPopup
        if (button == Input.Buttons.RIGHT) {
            if (highlightMode != Highlighting.Companion.HighlightModes.NORMAL) {
                cancelSkill.invoke()
                return true
            }

            val popup: Table
            // TODO ECS ITEM
//            if (level!!.getTopItem(tile.x, tile.y) != null) {
//                val item = level!!.getTopItem(tile.x, tile.y)!!
//                popup =
//                    if (item is EquipmentItem)
//                        EquipmentComparisonPopup(item)
//                    else
//                        ItemDetailsPopup(item)
//            }
//            else
//                popup = EntityLookupPopup(level!!.map[tile.y][tile.x], level!!.characterMap[tile.y][tile.x])
//            this.addActor(popup)
//            popup.setPosition(touch.x, touch.y)
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

    var moveDirection: Int? = null

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.TAB -> {
                showEq = true
            }
            Input.Keys.NUM_2 -> {
                actors.forEach { if (it is LevelDrawer) it.fogOfWar.drawFovFow += 1 }
            }
            Input.Keys.ESCAPE -> {
                if (highlightMode != Highlighting.Companion.HighlightModes.NORMAL)
                    cancelSkill.invoke()
            }
            // Movement
            Input.Keys.W, Input.Keys.UP -> {
                moveDirection = when (moveDirection) {
                    4 -> 7
                    6 -> 9
                    else -> 8
                }
            }
            Input.Keys.S, Input.Keys.DOWN -> {
                moveDirection = when (moveDirection) {
                    4 -> 1
                    6 -> 3
                    else -> 2
                }
            }
            Input.Keys.D, Input.Keys.RIGHT -> {
                moveDirection = when (moveDirection) {
                    8 -> 9
                    2 -> 3
                    else -> 6
                }
            }
            Input.Keys.A, Input.Keys.LEFT -> {
                moveDirection = when (moveDirection) {
                    8 -> 7
                    2 -> 1
                    else -> 4
                }
            }
        }
        return true
    }

    override fun keyUp(keyCode: Int): Boolean {
        when (keyCode) {
            Input.Keys.W, Input.Keys.UP -> {
                moveDirection = when (moveDirection) {
                    8 -> null
                    7 -> 4
                    9 -> 6
                    else -> moveDirection
                }
            }
            Input.Keys.S, Input.Keys.DOWN -> {
                moveDirection = when (moveDirection) {
                    2 -> null
                    1 -> 4
                    3 -> 6
                    else -> moveDirection
                }
            }
            Input.Keys.D, Input.Keys.RIGHT -> {
                moveDirection = when (moveDirection) {
                    6 -> null
                    3 -> 2
                    9 -> 8
                    else -> moveDirection
                }
            }
            Input.Keys.A, Input.Keys.LEFT -> {
                moveDirection = when (moveDirection) {
                    4 -> null
                    1 -> 2
                    7 -> 8
                    else -> moveDirection
                }
            }
        }
        return super.keyUp(keyCode)
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        gameCamera.scroll(amountY)
        return true
    }

    val highlighting = Highlighting()
    var highlightMode: Highlighting.Companion.HighlightModes = Highlighting.Companion.HighlightModes.NORMAL
        set(value) {
            field = value
            highlighting.deHighlightOnHover()
        }
    var highlightRange: HasRange? = null
    var skillRange: HasRange? = null
    lateinit var cancelSkill: () -> Unit

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val coord = gameCamera.getTile(screenX, screenY)

//        when (highlightMode) {
//            Highlighting.Companion.HighlightModes.NORMAL -> {
//                if (LevelArrays.getDiscoveredAt(coord))
//                    highlighting.highlightOnHover(coord)
//            }
//            Highlighting.Companion.HighlightModes.AREA -> {
//                if (skillRange!!.isInRange(Player.get(Position::class)!!.getPosition(), coord))
//                    highlighting.highlightAttackArea(highlightRange!!, coord, false)
//                else
//                    highlighting.deHighlight(true)
//            }
//            Highlighting.Companion.HighlightModes.ONLY_CHARACTERS -> {
//                if (skillRange!!.isInRange(Player.get(Position::class)!!.getPosition(), coord))
//                    highlighting.highlightAttackArea(highlightRange!!, coord, true)
//                else
//                    highlighting.deHighlight(true)
//            }
//        }


        return super.mouseMoved(screenX, screenY)
    }
}