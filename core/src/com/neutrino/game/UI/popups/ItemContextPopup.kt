package com.neutrino.game.UI.popups

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraButton
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.graphics.utility.BackgroundColor
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
                            if (Player.eventArray.hasCooldown(CooldownType.FOOD)) {
                                val cooldownLabel = TextraLabel("[@Cozette][%600][*]Food is on cooldown", KnownFonts.getStandardFamily())
                                cooldownLabel.name = "cooldown"
                                parent.addActor(cooldownLabel)
                                val coords = localToParentCoordinates(Vector2(x, y))
                                cooldownLabel.setPosition(coords.x, coords.y + 8f)
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

                            if ((item as EquipmentItem).requirements.data.containsKey("character"))
                                (item.requirements.data["character"] as Data<Character>).setData(Player)
                            if (!item.requirements.checkAll()) {
                                val cooldownLabel = TextraLabel("[@Cozette][%600][*]Requirements are not met!", KnownFonts.getStandardFamily())
                                cooldownLabel.name = "requirements"
                                parent.addActor(cooldownLabel)
                                val coords = localToParentCoordinates(Vector2(x, y))
                                cooldownLabel.setPosition(coords.x, coords.y + 8f)
                                cooldownLabel.addAction(Actions.moveBy(0f, 36f, 1f))
                                cooldownLabel.addAction(
                                    Actions.sequence(
                                        Actions.fadeOut(1.25f),
                                        Actions.removeActor()))
                                return
                            }

                            val itemType = Player.equipment.setItem(item as EquipmentItem)
                            GlobalData.notifyObservers(GlobalDataType.EQUIPMENT, itemType)
                        }
                    })

                    add(equipButton).prefWidth(90f).prefHeight(40f)
                }
                is ItemType.USABLE -> {
                    val useButton = TextraButton("[%150][@Cozette]Use", Scene2DSkin.defaultSkin)
                    useButton.addListener(object: ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            if (event?.button != Input.Buttons.LEFT)
                                return
                            super.clicked(event, x, y)
                            Player.eventArray.forEach { println(it) }
                            if (Player.eventArray.hasCooldown(CooldownType.ITEM(item.name))) {
                                val cooldownLabel = TextraLabel("[@Cozette][%600][*]This item is on cooldown", KnownFonts.getStandardFamily())
                                cooldownLabel.name = "cooldown"
                                parent.addActor(cooldownLabel)
                                val coords = localToParentCoordinates(Vector2(x, y))
                                cooldownLabel.setPosition(coords.x, coords.y + 8f)
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