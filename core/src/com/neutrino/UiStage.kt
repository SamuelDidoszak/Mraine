package com.neutrino

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.actors.centerPosition
import ktx.scene2d.image
import ktx.scene2d.scene2d
import ktx.scene2d.stack
import ktx.scene2d.table


class UiStage(
    viewport: Viewport
): Stage(viewport) {
    lateinit var eqScreen: ScrollPane

    var showEq: Boolean = true

    fun addactor() {
        val borderWidth: Int = 13
        val borderHeight: Int = 13
//        val padding: Int = 4

        val color = Color(238f, 195f, 154f, 1f)
        val table = scene2d.table {
                pad(0f)
                this.setFillParent(false)
                clip(true)
                for (n in 0 until 13) {
                    for (i in 0 until 12)
                        add(stack {
                            name = (n * 12 + i).toString()
                            image(Texture("items/knife.png"))
                        }).size(64f, 64f).left().bottom().padRight(if (i != 11) 4f else 0f).space(0f)
                    row().padTop(if (n != 12) 4f else 0f).space(0f)
                }
        }
        eqScreen = ScrollPane(table)
        eqScreen.name = "scrollEq"
        // without this line, scrollPane generously adds idiotic and undeletable empty space for each column with children in it
        eqScreen.setScrollingDisabled(true, false)
        eqScreen.setOverscroll(false, false)
        eqScreen.setScrollbarsVisible(false)
        eqScreen.layout()


        val backgroundImage = Image(Texture("UI/EquipmentCut.png"))
        this.addActor(backgroundImage)
        backgroundImage.name = "background"
        backgroundImage.centerPosition()

        this.addActor(eqScreen)
        eqScreen.width = backgroundImage.width - 2 * borderWidth
        eqScreen.height = backgroundImage.height - 2 * borderHeight
        eqScreen.centerPosition()

        println(backgroundImage.width.toString() + ", " + backgroundImage.height)
        println(eqScreen.width.toString() + ", " + eqScreen.height)
    }

    // input processor
    var timeClicked: Long = 0
    var clickedItem: Actor? = null
    var dragItem: Boolean? = null

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!(button != Input.Buttons.LEFT || button != Input.Buttons.RIGHT) || pointer > 0) return false
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )
        clickedItem = hit(coord.x, coord.y, true)

        timeClicked = TimeUtils.millis()
        dragItem = null
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )
        if (dragItem == true)
            parseItemDrop(coord.x, coord.y)
        clickedItem = null
        return super.touchUp(screenX, screenY, pointer, button)
    }

    private fun parseItemDrop(x: Float, y: Float) {
        if (clickedItem == null)
            return

        val inEq = (x in eqScreen.x .. eqScreen.x + eqScreen.width - 1 &&
            y in eqScreen.y .. eqScreen.y + eqScreen.height - 1)
        if (!inEq) {
            if (clickedItem != null)
                println("//TODO drop item out of eq")
            return
        }

        val coord: Vector2 = eqScreen.stageToLocalCoordinates(
            Vector2(x, y)
        )

        var hitChild = eqScreen.hit(coord.x, coord.y, false)

        // space between cells was hit
        if (hitChild is Table)
            return

        while (hitChild !is Stack)
            hitChild = hitChild.parent

        this.actors.removeValue(clickedItem, true)
        (hitChild as Stack).add(clickedItem)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (clickedItem == null)
            return false

        if (dragItem == null) {
            dragItem = TimeUtils.millis() - timeClicked >= 650
            if (dragItem!!) {
                clickedItem!!.name = "movedItem"
                clickedItem!!.parent.removeActor(clickedItem)
                addActor(clickedItem)
                clickedItem = actors.find { it.name == "movedItem" }!!
                clickedItem!!.name = null
            }
        }

        if (dragItem!!) {
            val coord: Vector2 = screenToStageCoordinates(
                Vector2(screenX.toFloat(), screenY.toFloat())
            )
            clickedItem!!.setPosition(coord.x - clickedItem!!.width / 2,coord.y - clickedItem!!.height / 2)
            return true
        }

        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.TAB -> {
                showEq = false
            }
        }
        return true
    }
}