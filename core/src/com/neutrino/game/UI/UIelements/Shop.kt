package com.neutrino.game.UI.UIelements

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.neutrino.game.UI.utility.EqActor
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.utility.Inventory
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table

class Shop(private val uiElements: Map<String, TextureAtlas.AtlasRegion>): Group() {

    private val shopBorder: Image = Image(uiElements["ShopBorder"])
    private val borderSize = 12

    val shopInventoryPane = ScrollPane(Table())

    private var rows = 6

    fun initialize(inventory: Inventory?) {
        addInventoryPane(inventory)
        addActor(shopInventoryPane)
        addActor(shopBorder)
        shopBorder.name = "shopBorder"

        width = shopBorder.width - 2 * (borderSize - 2)
        height = shopBorder.height - 2 * borderSize + 4
        shopInventoryPane.width = width
        shopInventoryPane.height = height
        shopInventoryPane.isVisible = true
    }

    private fun addInventoryPane(inventory: Inventory?) {
        rows =
            if (inventory == null)
                6
            else
                inventory.size / 10 + if (inventory.size % 10 != 0) 1 else 0
        rows = if (rows < 6) 6 else rows
//        println("Rows: $rows")
        val table = scene2d.table {
            for (n in 0 until 6) {
//                println("First, $n")
                for (i in 0 until 6) {
//                    println("Second, $i")
                    add(container {
                        val cellNumber = n * 6 + i
                        name = (cellNumber).toString()
                        background = getCellDrawable(cellNumber)
                        align(Align.bottomLeft)
                    }).size(84f, 84f).space(0f)
                }
                row().space(0f)
            }
        }
        table.pack()
        shopInventoryPane.actor = table
        name = "inventory"
        // without this line, scrollPane generously adds idiotic and undeletable empty space for each column with children in it
        shopInventoryPane.setScrollingDisabled(true, false)
        shopInventoryPane.setOverscroll(false, false)
        shopInventoryPane.setScrollbarsVisible(false)
        shopInventoryPane.layout()
    }

    fun setInventory(inventory: Inventory) {
        val inventoryRows = inventory.size / 10 + if (inventory.size % 10 != 0) 1 else 0
        if (inventoryRows > rows)
            addInventoryPane(inventory)

        (shopInventoryPane.actor as Table).children.forEach {
            (it as Container<*>).actor = null
            val cellNumber = it.name.toInt()
            if (cellNumber < inventory.itemList.size)
                it.actor = EqActor(inventory.itemList[cellNumber].item)
        }
    }

    private fun getCellDrawable(cellNumber: Int): Drawable {
        println("Called for $cellNumber")
        if (cellNumber >= Player.inventory.size)
            return TextureRegionDrawable(uiElements["cellUnavailable"])
        if (cellNumber == 0)
            return TextureRegionDrawable(uiElements["cellTopLeft"])
        if (cellNumber < 5)
            return TextureRegionDrawable(uiElements["cellTop"])
        if (cellNumber == 5)
            return TextureRegionDrawable(uiElements["cellTopRight"])
        val bottomRowNumber = cellNumber - (rows - 1) * 6
        if (bottomRowNumber == 0 )
            return TextureRegionDrawable(uiElements["cellBottomLeft"])
        if (bottomRowNumber in 1 until 5 )
            return TextureRegionDrawable(uiElements["cellBottom"])
        if (bottomRowNumber == 5 )
            return TextureRegionDrawable(uiElements["cellBottomRight"])
        if (cellNumber % 10 == 0)
            return TextureRegionDrawable(uiElements["cellLeft"])
        if (cellNumber % 10 == 5)
            return TextureRegionDrawable(uiElements["cellRight"])
        return TextureRegionDrawable(uiElements["cellMiddle"])
    }
}