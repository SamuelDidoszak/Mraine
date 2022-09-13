package com.neutrino

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.Viewport
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.equipment.EqActor
import ktx.actors.centerPosition
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table


class UiStage(
    viewport: Viewport
): Stage(viewport) {
    lateinit var eqScreen: ScrollPane

    var showEq: Boolean = true
    var refreshEq = false
        set(value) {
            if (value)
                refreshEquipment()
            field = false
        }

    var screenRatio: Float = width / height
    var zoomLevel: Float = 1.25f

    fun updateRatio() {
        screenRatio = viewport.screenWidth / viewport.screenHeight.toFloat()
    }

    fun addactor() {
        val borderWidth: Int = 13
        val borderHeight: Int = 13
//        val padding: Int = 4

        val color = Color(238f, 195f, 154f, 1f)

        val rows = Player.equipmentSize / 9 + if (Player.equipmentSize % 9 != 0) 1 else 0
        val table = scene2d.table {
                pad(0f)
                this.setFillParent(false)
                clip(true)
                for (n in 0 until rows) {
                    for (i in 0 until 9)
                        add(container {
                            val cellNumber = n * 9 + i
                            name = (cellNumber).toString()
                            align(Align.bottomLeft)
                            if (cellNumber < Player.equipment.itemList.size)
//                                actor = EqActor(Knife())
                                actor = EqActor(Player.equipment.itemList[cellNumber].item)
                        }).size(64f * zoomLevel, 64f * zoomLevel).left().bottom().padRight(if (i != 8) 4f else 0f).space(0f)
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


        val backgroundImage = Image(Texture("UI/equipmentSmaller.png"))
        this.addActor(backgroundImage)
        backgroundImage.name = "background"
        backgroundImage.centerPosition()

        this.addActor(eqScreen)
        eqScreen.width = backgroundImage.width - 2 * borderWidth
        eqScreen.height = backgroundImage.height - 2 * borderHeight
        eqScreen.centerPosition()

        backgroundImage.setSize(backgroundImage.width * zoomLevel, backgroundImage.height * zoomLevel)
        eqScreen.setSize(eqScreen.width * zoomLevel, eqScreen.height * zoomLevel)
        backgroundImage.centerPosition()
        eqScreen.centerPosition()

//        eqScreen.setDebug(true, true)
    }

    private fun refreshEquipment() {
        (eqScreen.actor as Table).children.forEach {
            (it as Container<*>).actor = null
            val cellNumber = it.name.toInt()
            if (cellNumber < Player.equipment.itemList.size)
                it.actor = EqActor(Player.equipment.itemList[cellNumber].item)
        }
    }

    // input processor
    var timeClicked: Long = 0
    var originalContainer: Container<*>? = null
    // possibly change to EqActor
    var clickedItem: Actor? = null
    var dragItem: Boolean? = null

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!(button != Input.Buttons.LEFT || button != Input.Buttons.RIGHT) || pointer > 0) return false
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )
        val clickedEq = getEqClicked(coord.x, coord.y)
        if (clickedEq != null) {
            clickedItem = getEqCell(coord.x, coord.y, clickedEq)?.actor
            if (clickedItem != null) {
                timeClicked = TimeUtils.millis()
                dragItem = null
            }
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (clickedItem == null)
            return false

        if (dragItem == null) {
            dragItem = TimeUtils.millis() - timeClicked >= 650
            if (dragItem!!) {
                clickedItem!!.name = "movedItem"
                originalContainer = clickedItem!!.parent as Container<*>
                originalContainer!!.removeActor(clickedItem)
                addActor(clickedItem)
                clickedItem = actors.find { it.name == "movedItem" }!!
                clickedItem!!.name = null
            }
        }

        if (dragItem!!) {
            val coord: Vector2 = screenToStageCoordinates(
                Vector2(screenX.toFloat(), screenY.toFloat())
            )
            clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * 2, coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * 2)
            return true
        }

        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )
        if (dragItem == true)
            parseItemDrop(coord.x, coord.y)
        clickedItem = null
        originalContainer = null
        return super.touchUp(screenX, screenY, pointer, button)
    }

    private fun getEqClicked(x: Float, y: Float): ScrollPane? {
        val inPlayerEq = (x in eqScreen.x .. eqScreen.x + eqScreen.width - 1 &&
                y in eqScreen.y .. eqScreen.y + eqScreen.height - 1)
        if (inPlayerEq)
            return eqScreen
        else
            return null
    }

    private fun getEqCell(x: Float, y: Float, clickedEq: ScrollPane): Container<*>? {
        val coord: Vector2 = clickedEq.stageToLocalCoordinates(
            Vector2(x, y)
        )

        var clickedChild = clickedEq.hit(coord.x, coord.y, false)

        // space between cells was hit
        if (clickedChild is Table)
            return null

        while (clickedChild !is Container<*>) {
            println(clickedChild.javaClass)
            clickedChild = clickedChild.parent
        }
        return clickedChild
    }

    private fun parseItemDrop(x: Float, y: Float) {
        if (clickedItem == null)
            return

        val clickedEq = getEqClicked(x, y)
        if (clickedEq == null) {
            if (clickedItem != null)
                println("//TODO drop item out of eq")
            return
        }
        // if the area between cells was clicked, reset the item position
        val container = getEqCell(x, y, clickedEq)
        if (container == null) {
            originalContainer!!.actor = clickedItem
            return
        }

        this.actors.removeValue(clickedItem, true)
        // check if the cell is ocupied and act accordingly
        if (container.hasChildren()) {
            // sum item amounts
            if ((clickedItem as EqActor).item.name == (container.actor as EqActor).item.name
                && (container.actor as EqActor).item.stackable) {
                    (container.actor as EqActor).item.amount =
                        (container.actor as EqActor).item.amount?.plus((clickedItem as EqActor).item.amount!!
                    )
                return
            } else
                originalContainer!!.actor = container.actor as EqActor
        }
        container.actor = (clickedItem)
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.TAB -> {
                showEq = false
            }
        }
        return true
    }

    override fun draw() {
        if (Player.equipmentSizeChanged) {
            Player.equipmentSizeChanged = false
            actors.removeValue(eqScreen, true)
            addactor()
        }
        super.draw()
    }
}