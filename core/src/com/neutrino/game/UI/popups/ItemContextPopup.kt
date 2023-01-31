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
import com.neutrino.game.domain.model.items.*
import com.neutrino.game.domain.model.systems.event.CausesCooldown
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.graphics.utility.BackgroundColor
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.table

class ItemContextPopup(
    val usedItemList:  ArrayDeque<Item>,
    val useOnSetter: (item: Item) -> Unit,
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
                            if (Player.eventArray.hasCooldown((item as? CausesCooldown)?.cooldownType)) {
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
                    if (item.useOn != UseOn.OTHERS_ONLY)
                        add(eatButton).prefWidth(90f).prefHeight(40f)

                    addUseOn(item, this)
                }
                is SkillBook -> {
                    val learnButton = TextraButton("[%150][@Cozette]Learn", Scene2DSkin.defaultSkin)
                    learnButton.addListener(object: ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            if (event?.button != Input.Buttons.LEFT)
                                return
                            super.clicked(event, x, y)

                            if (item.skill.requirement.data.containsKey("character"))
                                (item.skill.requirement.data["character"] as Data<Character>).setData(Player)
                            if (!item.skill.requirement.checkAll()) {
                                val requirementLabel = TextraLabel("[@Cozette][%600][*]Requirements are not met!", KnownFonts.getStandardFamily())
                                requirementLabel.name = "requirements"
                                parent.addActor(requirementLabel)
                                val coords = localToParentCoordinates(Vector2(x, y))
                                requirementLabel.setPosition(coords.x, coords.y + 8f)
                                requirementLabel.addAction(Actions.moveBy(0f, 36f, 1f))
                                requirementLabel.addAction(
                                    Actions.sequence(
                                        Actions.fadeOut(1.25f),
                                        Actions.removeActor()))
                                return
                            }
                            if (Player.skillList.find { it::class == item.skill::class } != null) {
                                val skillLearntLabel = TextraLabel("[@Cozette][%600][*]Skill is already learnt", KnownFonts.getStandardFamily())
                                skillLearntLabel.name = "skillLearnt"
                                parent.addActor(skillLearntLabel)
                                val coords = localToParentCoordinates(Vector2(x, y))
                                skillLearntLabel.setPosition(coords.x, coords.y + 8f)
                                skillLearntLabel.addAction(Actions.moveBy(0f, 36f, 1f))
                                skillLearntLabel.addAction(
                                    Actions.sequence(
                                        Actions.fadeOut(1.25f),
                                        Actions.removeActor()))
                                return
                            }

                            usedItemList.add(item)
                            customUseMethod.invoke()
                        }
                    })

                    if (item.useOn != UseOn.OTHERS_ONLY)
                        add(learnButton).prefWidth(90f).prefHeight(40f)
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
                                val requirementsLabel = TextraLabel("[@Cozette][%600][*]Requirements are not met!", KnownFonts.getStandardFamily())
                                requirementsLabel.name = "requirements"
                                parent.addActor(requirementsLabel)
                                val coords = localToParentCoordinates(Vector2(x, y))
                                requirementsLabel.setPosition(coords.x, coords.y + 8f)
                                requirementsLabel.addAction(Actions.moveBy(0f, 36f, 1f))
                                requirementsLabel.addAction(
                                    Actions.sequence(
                                        Actions.fadeOut(1.25f),
                                        Actions.removeActor()))
                                return
                            }

                            val itemType = Player.equipment.setItem(item as EquipmentItem)
                            GlobalData.notifyObservers(GlobalDataType.EQUIPMENT, itemType)
                            customUseMethod.invoke()
                        }
                    })

                    if (item !is ItemType.USABLE || item.useOn != UseOn.OTHERS_ONLY)
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
                            if (Player.eventArray.hasCooldown((item as? CausesCooldown)?.cooldownType)) {
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

                    if (item.useOn != UseOn.OTHERS_ONLY)
                        add(useButton).prefWidth(90f).prefHeight(40f)

                    addUseOn(item, this)
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

    private fun addUseOn(item: Item, table: Table) {
        val canUseOn = (item as ItemType.USABLE).useOn == UseOn.SELF_AND_OTHERS || item.useOn == UseOn.TILE || item.useOn == UseOn.OTHERS_ONLY
        if (!canUseOn)
            return

        val useOnButton = TextraButton("[%150][@Cozette]Use on", Scene2DSkin.defaultSkin)
        useOnButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (event?.button != Input.Buttons.LEFT)
                    return
                super.clicked(event, x, y)
                useOnSetter.invoke(item)
                customUseMethod.invoke()
            }
        })
        table.add(useOnButton).prefWidth(90f).prefHeight(40f)
    }
}