package com.neutrino.game.UI.popups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.domain.model.items.SkillBook
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.items.Item
import com.neutrino.game.entities.items.attributes.Amount
import com.neutrino.game.entities.items.attributes.GoldValue
import com.neutrino.game.entities.items.attributes.usable.EquipEvents
import com.neutrino.game.entities.items.attributes.usable.UseOnEntity
import com.neutrino.game.entities.shared.attributes.Description
import com.neutrino.game.entities.systems.requirements.PrintableInfo
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.graphics.utility.ColorUtils.toTextraColor
import com.neutrino.game.util.Constants
import com.neutrino.game.util.Fonts
import com.neutrino.game.util.add
import ktx.scene2d.Scene2DSkin

class ItemDetailsPopup(val item: Item, private val showDescription: Boolean = true): Table() {
    init {
//        if (item has SkillBook)
//            skillBookPopup()
//        else
            regularItemPopup()

        val goldImage = Image(Textures.get("gold1").texture)
        val goldValue = TextraLabel( "[%75]" + Color.BLACK.toTextraColor() + item.get(GoldValue::class)!!.value, Fonts.EQUIPMENT)
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

        add(itemName).growX().center().colspan(10)
        if (item.get(Amount::class)!!.amount > 1)
            itemName.setText(itemName.storedText + " x${item.get(Amount::class)!!.amount}")

        if (item has Description::class) {
            val description = TextraLabel("[%75]" + item.get(Description::class)!!.description, Fonts.MATCHUP, Color.BLACK)
            description.wrap = true
            description.alignment = Align.left
            row().space(12f)
            add(description).growX().colspan(10)
        }

        row().padTop(8f).padBottom(0f)


        val eventAttributes = ArrayList<Attribute>()
        eventAttributes.add(item.get(UseOnEntity::class))
        eventAttributes.add(item.get(EquipEvents::class))
        for (attribute in eventAttributes) {
            val printableList = (attribute as PrintableInfo<Attribute>).getPrintableInfo(null)
            for (printable in printableList) {
                val value = TextraLabel("[%75]" + printable.first, Fonts.MATCHUP, Color.BLACK)
                value.wrap = true
                value.alignment = Align.center
                add(value).center().growX().colspan(10).spaceBottom(8f)
                row()

                val valueLabel = TextraLabel("[%75]" + printable.second.toString(), Fonts.MATCHUP)
                valueLabel.alignment = Align.left
                valueLabel.wrap = true
                add(valueLabel).left().growX().colspan(10).spaceBottom(8f)
                row()
            }
        }

        val requirements: ArrayList<Pair<String, String>> = ArrayList()
        item.get(Requirements.Stats::class)?.print(Player)?.forEach { requirements.add(it) }
        item.get(Requirements.Custom::class)?.print(Player)?.forEach { requirements.add(it) }
        if (requirements.isNotEmpty()) {
            add(TextraLabel("Requirements", Fonts.EQUIPMENT, Color.BLACK)).expandX().center().colspan(10).spaceTop(12f).spaceBottom(12f)
            row()
        }

        for (requirement in requirements) {
            val dataLabel = TextraLabel("[%75]" + requirement.first, Fonts.MATCHUP, Color.BLACK)
            dataLabel.wrap = true
            dataLabel.alignment = Align.left
            val valueLabel = TextraLabel("[%75]" + requirement.second, Fonts.MATCHUP, Color.BLACK)
            add(dataLabel).growX().spaceBottom(8f)
            add(valueLabel).right().spaceBottom(8f)
            row()
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