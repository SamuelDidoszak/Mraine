package com.neutrino.game.presentation.utility

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType

class ItemDetailsPopup(val item: Item, private val showDescription: Boolean = true): Table() {
    init {
        this.width = 360f
        padTop(0f)
        padBottom(0f)
        align(Align.left)
        pad(8f)
        add(TextraLabel("[%125]" + item.name + if (item.amount != null && item.amount!! > 1) " x ${item.amount}" else "", KnownFonts.getStandardFamily())).colspan(3).expandX().center()
        if (showDescription) {
            row().pad(4f).colspan(3).fillX()
            add(TextraLabel("[%75]" + item.description, KnownFonts.getStandardFamily()).setWrap(true)).maxWidth(
                Value.percentWidth(0.9f, this)).colspan(3).center().pad(0f)
            row().pad(4f).colspan(3)
        }
        else
            row().pad(4f).colspan(3)
        when (item) {
            is ItemType.EDIBLE -> {
                add(TextraLabel("Total heal:", KnownFonts.getStandardFamily())).colspan(2).left()
                add(TextraLabel(ValueComparison().compareStats(item.power * item.repeats, item.powerOg * item.repeatsOg) + (item.power * item.repeats).toString(), KnownFonts.getStandardFamily())).colspan(1).center()
                row().pad(4f).colspan(3)
                add(TextraLabel("Power:", KnownFonts.getStandardFamily())).colspan(2).left()
                add(TextraLabel(ValueComparison().compareStats(item.power, item.powerOg) + item.power.toString(), KnownFonts.getStandardFamily())).colspan(1).center()
                row().pad(4f).colspan(3)
                add(TextraLabel("Speed:", KnownFonts.getStandardFamily())).colspan(2).left()
                add(TextraLabel(ValueComparison().compareStats(item.speed, item.speedOg) + item.speed.toString(), KnownFonts.getStandardFamily())).colspan(1).center()
                row().pad(4f).colspan(3)
                add(TextraLabel("Repeats:", KnownFonts.getStandardFamily())).colspan(2).left()
                add(TextraLabel(ValueComparison().compareStats(item.repeats, item.repeatsOg) + item.repeats.toString(), KnownFonts.getStandardFamily())).colspan(1).center()
            }
        }
        this.name = "itemDetails"
        this.pack()
        this.width = 360f
    }

    /** Made as a separate function in order to create the texture in the right place. Otherwise, it shows up for a split second at 0, 0 screen coordinates */
    fun assignBg(x: Float, y: Float) {
        val bgColor: BackgroundColor = BackgroundColor("UI/whiteColorTexture.png", x, y, width, height)
        bgColor.setColor(0, 0, 0, 160)
        this.background = bgColor
    }
}