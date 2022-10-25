package com.neutrino.game.graphics.utility

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraButton
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.turn.CooldownType
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.table

class ItemContextPopup(
    val usedItemList:  ArrayDeque<Item>,
    val customUseMethod: () -> Unit? = {}
) {
    fun createContextMenu(item: Item, x: Float, y: Float): Table? {
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
                                cooldownLabel.addAction(
                                    Actions.sequence(
                                    Actions.fadeOut(1.25f),
                                    Actions.removeActor()))
                                return
                            }
                            usedItemList.add(item)
                            customUseMethod.invoke()
                        }
                    })
                    add(eatButton).prefWidth(90f).prefHeight(40f)
                }
                is ItemType.MISC -> return null
                is ItemType.KEY -> return null
                is ItemType.EQUIPMENT -> {
                    val equipButton = TextraButton("[%150][@Cozette]Equip", Scene2DSkin.defaultSkin)
                    equipButton.addListener(object: ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            if (event?.button != Input.Buttons.LEFT)
                                return
                            super.clicked(event, x, y)
                            Player.equipment.setItem(item as EquipmentItem)
                        }
                    })

                    add(equipButton).prefWidth(90f).prefHeight(40f)
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
                                cooldownLabel.addAction(
                                    Actions.sequence(
                                    Actions.fadeOut(1.25f),
                                    Actions.removeActor()))
                                return
                            }
                            usedItemList.add(item)
                            customUseMethod.invoke()
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
        table.name = "itemContextPopup"

        return table
    }
}