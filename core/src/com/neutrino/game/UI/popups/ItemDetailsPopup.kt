package com.neutrino.game.UI.popups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.Constants
import com.neutrino.game.Fonts
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.items.SkillBook
import com.neutrino.game.graphics.utility.ColorUtils.toTextraColor
import ktx.scene2d.Scene2DSkin

class ItemDetailsPopup(val item: Item, private val showDescription: Boolean = true): Table() {
    init {
        when (item) {
            is SkillBook -> skillBookPopup()
            else -> regularItemPopup()
        }

        val goldImage = Image(Constants.DefaultIconTexture.findRegion("gold"))
        val goldValue = TextraLabel( "[%75]" + Color.BLACK.toTextraColor() + item.goldValue, Fonts.EQUIPMENT)
        goldValue.alignment = Align.bottom
        val goldGroup = Table()
        goldGroup.add(goldImage).size(24f)
        goldGroup.add(goldValue).spaceLeft(8f).bottom().pad(0f)
        add(goldGroup).expandX().colspan(10).right()

        background = Scene2DSkin.defaultSkin.getDrawable("stretchableCell")
        name = "itemDetails"
        width = 256f
        layout()
        height = minHeight
        layout()
    }

    private fun regularItemPopup() {
        val itemName = TextraLabel("[BLACK]" + item.name, Fonts.EQUIPMENT)
        itemName.wrap = true
        itemName.alignment = Align.center

        val description = TextraLabel("[%75]" + item.description, Fonts.MATCHUP, Color.BLACK)
        description.wrap = true
        description.alignment = Align.left

        add(itemName).growX().center().colspan(10)
        if (item.amount != null && item.amount!! > 1)
            itemName.setText(itemName.storedText + " x${item.amount}")

        if (item.description.isNotEmpty()) {
            row().space(12f)
            add(description).growX().colspan(10)
        }

        row().padTop(8f).padBottom(0f)

        when (item) {
            is ItemType.EDIBLE -> {
                add(TextraLabel("[%75][BLACK]Total heal:", Fonts.EQUIPMENT)).left().growX()
                add(TextraLabel("[%75]" + ValueComparison().compareStats(item.power * item.executions, item.powerOg * item.executionsOg) + (item.power * item.executions).toString(), Fonts.EQUIPMENT)).colspan(1).right()
                row().space(8f)

                val label = TextraLabel(
                    "[%75][BLACK]Heals " +
                        ValueComparison().compareStats(item.power, item.powerOg) + item.power.toString() +
                        " [BLACK]per " +
                        ValueComparison().compareStats(item.timeout, item.timeoutOg) + item.timeout.toString() +
                        " [BLACK]turn " +
                        ValueComparison().compareStats(item.executions, item.executionsOg) + item.executions.toString() +
                        " [BLACK]times"
                , Fonts.MATCHUP)
                label.wrap = true
                label.alignment = Align.left

                add(label).growX().colspan(10)
                row()
            }
            else -> {
                // Remove space added by previous row
                row().padTop(0f)
            }
        }
    }

    private fun skillBookPopup() {
        val skill = (item as SkillBook).skill
        val skillImage = Image(TextureRegion(Constants.DefaultIconTexture.findRegion(skill.textureName)))
        val skillName = TextraLabel(item.name, Fonts.EQUIPMENT)
        skillName.wrap = true
        skillName.alignment = Align.center
        val description = TextraLabel("[%75]" + skill.description, Fonts.MATCHUP, Color.BLACK)
        description.wrap = true
        description.alignment = Align.left

        add(skillImage).size(64f).colspan(10).padTop(0f)
        row().space(12f)
        add(skillName).growX().center().colspan(10)
        row().space(12f)
        add(description).growX().colspan(10)

        row().padTop(12f).padBottom(0f)

        for (data in skill.printableData) {
            val dataLabel = TextraLabel("[%75]" + data.first, Fonts.MATCHUP, Color.BLACK)
            dataLabel.wrap = true
            dataLabel.alignment = Align.left
            val valueLabel = TextraLabel("[%75]" + data.second.invoke().toString(), Fonts.MATCHUP, Color.BLACK)
            add(dataLabel).growX()
            add(valueLabel)
            row().space(8f)
        }

        row().padTop(12f)
        row().space(8f).padBottom(0f)

        add(TextraLabel("Requirements", Fonts.EQUIPMENT, Color.BLACK)).expandX().center().colspan(10)

        row().padTop(12f).padBottom(0f)

        for (data in skill.requirement.getPrintable(true)) {
            val dataLabel = TextraLabel("[%75]" + data.first, Fonts.MATCHUP, Color.BLACK)
            dataLabel.wrap = true
            dataLabel.alignment = Align.left
            val valueLabel = TextraLabel("[%75]" + data.second, Fonts.MATCHUP, Color.BLACK)
            add(dataLabel).growX()
            add(valueLabel)
            row().space(8f)
        }
        row().padTop(12f).padBottom(0f)
    }
}