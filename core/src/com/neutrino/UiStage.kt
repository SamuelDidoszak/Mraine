package com.neutrino

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraButton
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.items.equipment.utility.EqActor
import com.neutrino.game.domain.model.items.equipment.utility.Inventory
import com.neutrino.game.domain.model.turn.CooldownType
import com.neutrino.game.graphics.utility.BackgroundColor
import com.neutrino.game.graphics.utility.ItemDetailsPopup
import ktx.actors.centerPosition
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table


class UiStage(
    viewport: Viewport
): Stage(viewport) {
    lateinit var invScreen: ScrollPane

    /** FIFO of dropped items */
    val itemDropList: ArrayDeque<Item> = ArrayDeque()

    /** FIFO of used item actions */
    val usedItemList: ArrayDeque<Item> = ArrayDeque()

    var showInventory: Boolean = true
    var refreshInventory = false
        set(value) {
            if (value)
                refreshInventory()
            field = false
        }

    var screenRatio: Float = width / height
    var zoomLevel: Float = 1.25f

    fun updateRatio() {
        screenRatio = viewport.screenWidth / viewport.screenHeight.toFloat()
    }

    fun addInventoryActor() {
        val borderWidth: Int = 13
        val borderHeight: Int = 13
//        val padding: Int = 4

        val color = Color(238f, 195f, 154f, 1f)

        val rows = Player.inventorySize / 9 + if (Player.inventorySize % 9 != 0) 1 else 0
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
                            if (cellNumber < Player.inventory.itemList.size)
                                actor = EqActor(Player.inventory.itemList[cellNumber].item)
                        }).size(64f * zoomLevel, 64f * zoomLevel).left().bottom().padRight(if (i != 8) 4f else 0f).space(0f)
                    row().padTop(if (n != 12) 4f else 0f).space(0f)
                }
        }
        table.pack()
        invScreen = ScrollPane(table)
        invScreen.name = "playerEq"
        // without this line, scrollPane generously adds idiotic and undeletable empty space for each column with children in it
        invScreen.setScrollingDisabled(true, false)
        invScreen.setOverscroll(false, false)
        invScreen.setScrollbarsVisible(false)
        invScreen.layout()


        val backgroundImage = Image(Texture("UI/equipmentSmaller.png"))
        addActor(backgroundImage)
        backgroundImage.name = "background"
        backgroundImage.centerPosition()

        addActor(invScreen)
        invScreen.width = backgroundImage.width - 2 * borderWidth
        invScreen.height = backgroundImage.height - 2 * borderHeight
        invScreen.centerPosition()

        backgroundImage.setSize(backgroundImage.width * zoomLevel, backgroundImage.height * zoomLevel)
        invScreen.setSize(invScreen.width * zoomLevel, invScreen.height * zoomLevel)
        backgroundImage.centerPosition()
        invScreen.centerPosition()
    }

    private fun createContextMenu(item: Item, x: Float, y: Float): Table? {
        val table = scene2d.table {
            align(Align.center)
            pad(8f)
            when (item) {
                is ItemType.EDIBLE -> {
                    val eatButton = TextraButton("[%150][@Cozette]Eat", Scene2DSkin.defaultSkin)
                    eatButton.addListener(object: ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            if (event?.button != Input.Buttons.LEFT)
                                return
                            super.clicked(event, x, y)
                            if (Player.hasCooldown(CooldownType.FOOD)) {
                                val cooldownLabel = TextraLabel("[@Cozette][%600][*]Food is on cooldown", KnownFonts.getStandardFamily())
                                cooldownLabel.name = "cooldown"
                                addActor(cooldownLabel)
                                cooldownLabel.setPosition(x, y + 8f)
                                cooldownLabel.addAction(Actions.moveBy(0f, 36f, 1f))
                                cooldownLabel.addAction(Actions.sequence(
                                    Actions.fadeOut(1.25f),
                                    Actions.removeActor()))
                                return
                            }
                            usedItemList.add(item)
                            showInventory = false
                            nullifyAllValues()
                        }
                    })
                    add(eatButton).prefWidth(90f).prefHeight(40f)
                }
                is ItemType.MISC -> return null
                is ItemType.KEY -> return null
                is ItemType.EQUIPMENT -> {
                    add(TextraButton("[%150][@Cozette]Equip", Scene2DSkin.defaultSkin)).prefWidth(90f).prefHeight(40f)
                }
                is ItemType.WEAPON -> {
                    add(TextraButton("[%150][@Cozette]Equip", Scene2DSkin.defaultSkin)).prefWidth(90f).prefHeight(40f)
                }
                is ItemType.SCROLL -> {
                    val useButton = TextraButton("[%150][@Cozette]Use", Scene2DSkin.defaultSkin)
                    useButton.addListener(object: ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            if (event?.button != Input.Buttons.LEFT)
                                return
                            super.clicked(event, x, y)
                            if (Player.hasCooldown(CooldownType.ITEM(item.name))) {
                                val cooldownLabel = TextraLabel("[@Cozette][%600][*]This scroll is on cooldown", KnownFonts.getStandardFamily())
                                cooldownLabel.name = "cooldown"
                                addActor(cooldownLabel)
                                cooldownLabel.setPosition(x, y + 8f)
                                cooldownLabel.addAction(Actions.moveBy(0f, 36f, 1f))
                                cooldownLabel.addAction(Actions.sequence(
                                    Actions.fadeOut(1.25f),
                                    Actions.removeActor()))
                                return
                            }
                            usedItemList.add(item)
                            showInventory = false
                            nullifyAllValues()
                        }
                    })
                    add(useButton).prefWidth(90f).prefHeight(40f)
                }
            }
            pack()
        }

        val bgColor: BackgroundColor = BackgroundColor("UI/whiteColorTexture.png", x, y, table.width, table.height)
        bgColor.setColor(0, 0, 0, 160)
        table.background = bgColor

        return table
    }

    private fun refreshInventory() {
        (invScreen.actor as Table).children.forEach {
            (it as Container<*>).actor = null
            val cellNumber = it.name.toInt()
            if (cellNumber < Player.inventory.itemList.size)
                it.actor = EqActor(Player.inventory.itemList[cellNumber].item)
        }
    }

    private var detailsPopup: Table? = null
    private var displayedItem: Item? = null
    private var contextPopup: Table? = null

    // input processor
    private var timeClicked: Long = 0
    private var originalContainer: Container<*>? = null
    private var originalInventory: Inventory? = null
    // possibly change to EqActor
    var clickedItem: Actor? = null
    private var dragItem: Boolean? = null
    private var itemClicked: Boolean? = null

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!(button != Input.Buttons.LEFT || button != Input.Buttons.RIGHT) || pointer > 0) return false
        if (button == Input.Buttons.RIGHT)
            return super.touchDown(screenX, screenY, pointer, button)

        if (itemClicked == true) {
            timeClicked = TimeUtils.millis()
            return super.touchDown(screenX, screenY, pointer, button)
        }

        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )
        // gets the eq ui and sets the originalEq
        val clickedInv = getInvClicked(coord.x, coord.y)
        if (clickedInv != null) {
            if (clickedInv.name == "playerEq")
                originalInventory = Player.inventory

            clickedItem = getInvCell(coord.x, coord.y, clickedInv)?.actor
            if (clickedItem != null)
                timeClicked = TimeUtils.millis()
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (clickedItem == null)
            return false

        if (dragItem == null) {
            dragItem = TimeUtils.millis() - timeClicked >= 650
            if (dragItem!!)
                pickUpItem()
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
        // Stop contextMenu click from propagating
        if (contextPopup != null && button == Input.Buttons.LEFT)
            return super.touchUp(screenX, screenY, pointer, button)

        if (button == Input.Buttons.LEFT) {
            if (detailsPopup != null)
                this.actors.removeValue(detailsPopup, true)

            if (dragItem == true)
                parseItemDrop(coord.x, coord.y)

            // Dropping the clicked item
            if (itemClicked == true && TimeUtils.millis() - timeClicked <= 200) {
                parseItemDrop(coord.x, coord.y)
                clickedItem = null
            }

            // The item was clicked
            if (clickedItem != null && TimeUtils.millis() - timeClicked <= 200) {
                pickUpItem()
                itemClicked = true
                clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * (4f * 1.25f * 1.2f) / 2,
                    coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * (4f * 1.25f * 1.2f) / 2)
                return super.touchUp(screenX, screenY, pointer, button)
            }
        }
        else if (button == Input.Buttons.RIGHT && clickedItem == null) {
            if (contextPopup != null) {
                this.actors.removeValue(contextPopup, true)
                contextPopup = null
            }
            else {
                val hoveredInv = getInvClicked(coord.x, coord.y)
                if (hoveredInv != null) {
                    val hoveredItem: Actor? = getInvCell(coord.x, coord.y, hoveredInv)?.actor
                    if (hoveredItem != null) {
                        contextPopup = createContextMenu((hoveredItem as EqActor).item, coord.x, coord.y)
                        if (contextPopup != null) {
                            if (detailsPopup != null)
                                this.actors.removeValue(detailsPopup, true)
                            addActor(contextPopup)
                            contextPopup?.setPosition(coord.x, coord.y)
                        }
                    }
                }
            }
        }

        clickedItem = null
        originalContainer = null
        originalInventory = null
        itemClicked = null
        dragItem = null
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )

        // create new popup
        val hoveredInv = getInvClicked(coord.x, coord.y)
        var hoveredItem: Actor? = null
        if (hoveredInv != null && contextPopup == null) {
            hoveredItem = getInvCell(coord.x, coord.y, hoveredInv)?.actor
            if (hoveredItem != null && (hoveredItem as EqActor).item != displayedItem) {
                if (detailsPopup != null)
                    this.actors.removeValue(detailsPopup, true)
                detailsPopup = ItemDetailsPopup(hoveredItem.item, true)
                detailsPopup!!.setPosition(coord.x, coord.y)
                displayedItem = hoveredItem.item
                addActor(detailsPopup)
                detailsPopup!!.setPosition(coord.x, coord.y)
                (detailsPopup!! as ItemDetailsPopup).assignBg(coord.x, coord.y)
            }
        }

        // delete or move the popup
        if (hoveredInv == null || hoveredItem == null) {
            displayedItem = null
            this.actors.removeValue(detailsPopup, true)
            detailsPopup = null
        } else {
            detailsPopup!!.setPosition(coord.x, coord.y)
        }

        if (itemClicked == true)
            clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * (4f * 1.25f * 1.2f) / 2,
                coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * (4f * 1.25f * 1.2f) / 2)
        return super.mouseMoved(screenX, screenY)
    }

    private fun pickUpItem() {
        clickedItem!!.name = "movedItem"
        originalContainer = clickedItem!!.parent as Container<*>
        originalContainer!!.removeActor(clickedItem)
        addActor(clickedItem)
        clickedItem = actors.find { it.name == "movedItem" }!!
        clickedItem!!.name = null
        clickedItem!!.setScale(1.2f, 1.2f)
    }

    /** Returns the eq ui and sets the originalEq */
    private fun getInvClicked(x: Float, y: Float): ScrollPane? {
        val inPlayerEq = (x in invScreen.x .. invScreen.x + invScreen.width - 1 &&
                y in invScreen.y .. invScreen.y + invScreen.height - 1)
        if (inPlayerEq) {
            return invScreen
        }
        else
            return null
    }

    private fun getInvCell(x: Float, y: Float, clickedEq: ScrollPane): Container<*>? {
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

        val clickedInv = getInvClicked(x, y)
        if (clickedInv == null) {
            // DROPPING THE ITEM
            if (clickedItem != null) {
                itemDropList.add((clickedItem as EqActor).item)
                // TODO change the remove implementation to this after adding the sorting and user defined positions
//                originalEq!!.itemList.removeAt(originalContainer!!.name.toInt())
                originalInventory!!.itemList.remove(
                    originalInventory!!.itemList.find { it.item == (clickedItem as EqActor).item }
                )

                clickedItem!!.addAction(Actions.scaleTo(0f, 0f, 0.35f))
                clickedItem!!.addAction(Actions.sequence(
                    Actions.fadeOut(0.35f),
                    Actions.removeActor()
                ))
            }
            return
        }
        clickedItem!!.setScale(1f, 1f)
        // if the area between cells was clicked, reset the item position
        val container = getInvCell(x, y, clickedInv)
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
                showInventory = false
                nullifyAllValues()
            }
        }
        return true
    }

    /** Sets all values to null */
    private fun nullifyAllValues() {
        clickedItem?.addAction(Actions.removeActor())
        clickedItem = null
        originalContainer = null
        originalInventory = null
        itemClicked = null
        dragItem = null
        if (detailsPopup != null) {
            this.actors.removeValue(detailsPopup, true)
            detailsPopup = null
        }
        displayedItem = null
        if (contextPopup != null) {
            this.actors.removeValue(contextPopup, true)
            contextPopup = null
        }
    }

    override fun draw() {
        if (Player.inventorySizeChanged) {
            Player.inventorySizeChanged = false
            actors.removeValue(invScreen, true)
            addInventoryActor()
        }
        super.draw()
    }
}