package com.neutrino.game.UI.UIelements

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.neutrino.game.UI.utility.EqActor
import com.neutrino.game.domain.model.characters.Player
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table

class Inventory(private val uiElements: Map<String, TextureAtlas.AtlasRegion>): ScrollPane(Table()) {

    val borderSize = 12

    var forceRefreshInventory: Boolean = false

    fun initialize() {
        var rows = Player.inventory.size / 10 + if (Player.inventory.size % 10 != 0) 1 else 0
        rows = if (rows < 6) 6 else rows
        val table = scene2d.table {
            this.setFillParent(false)
            clip(true)
            for (n in 0 until rows) {
                for (i in 0 until 10) {
                    add(container {
                        val cellNumber = n * 10 + i
                        name = (cellNumber).toString()
                        background = getCellDrawable(cellNumber, rows)
                        align(Align.bottomLeft)
                    }).size(84f, 84f).space(0f)
                }
                row().space(0f)
            }
        }
        table.pack()
        actor = table
        name = "inventory"
        // without this line, scrollPane generously adds idiotic and undeletable empty space for each column with children in it
        setScrollingDisabled(true, false)
        setOverscroll(false, false)
        setScrollbarsVisible(false)
        layout()
    }

    fun refreshInventory() {
        (actor as Table).children.forEach {
            (it as Container<*>).actor = null
            val cellNumber = it.name.toInt()
            if (cellNumber < Player.inventory.itemList.size)
                it.actor = EqActor(Player.inventory.itemList[cellNumber].item)
        }
    }

    private fun getCellDrawable(cellNumber: Int, rows: Int): Drawable {
        if (cellNumber >= Player.inventory.size)
            return TextureRegionDrawable(uiElements["cellUnavailable"])
        if (cellNumber == 0)
            return TextureRegionDrawable(uiElements["cellTopLeft"])
        if (cellNumber < 9)
            return TextureRegionDrawable(uiElements["cellTop"])
        if (cellNumber == 9)
            return TextureRegionDrawable(uiElements["cellTopRight"])
        val bottomRowNumber = cellNumber - (rows - 1) * 10
        if (bottomRowNumber == 0 )
            return TextureRegionDrawable(uiElements["cellBottomLeft"])
        if (bottomRowNumber in 1..8 )
            return TextureRegionDrawable(uiElements["cellBottom"])
        if (bottomRowNumber == 9 )
            return TextureRegionDrawable(uiElements["cellBottomRight"])
        if (cellNumber % 10 == 0)
            return TextureRegionDrawable(uiElements["cellLeft"])
        if (cellNumber % 10 == 9)
            return TextureRegionDrawable(uiElements["cellRight"])
        return TextureRegionDrawable(uiElements["cellMiddle"])
    }
}