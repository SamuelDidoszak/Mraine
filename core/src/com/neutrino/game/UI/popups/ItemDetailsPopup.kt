package com.neutrino.game.UI.popups

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType
import ktx.scene2d.Scene2DSkin

class ItemDetailsPopup(val item: Item, private val showDescription: Boolean = true): Table() {
    init {
        this.width = 360f
        align(Align.left)
        background = Scene2DSkin.defaultSkin.getDrawable("stretchableCell")
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
                add(TextraLabel(ValueComparison().compareStats(item.power * item.executions, item.powerOg * item.executionsOg) + (item.power * item.executions).toString(), KnownFonts.getStandardFamily())).colspan(1).right()
                row().pad(4f).colspan(3)
                add(TextraLabel("Power:", KnownFonts.getStandardFamily())).colspan(2).left()
                add(TextraLabel(ValueComparison().compareStats(item.power, item.powerOg) + item.power.toString(), KnownFonts.getStandardFamily())).colspan(1).right()
                row().pad(4f).colspan(3)
                add(TextraLabel("Speed:", KnownFonts.getStandardFamily())).colspan(2).left()
                add(TextraLabel(ValueComparison().compareStats(item.timeout, item.timeoutOg) + item.timeout.toString(), KnownFonts.getStandardFamily())).colspan(1).right()
                row().pad(4f).colspan(3)
                add(TextraLabel("Repeats:", KnownFonts.getStandardFamily())).colspan(2).left()
                add(TextraLabel(ValueComparison().compareStats(item.executions, item.executionsOg) + item.executions.toString(), KnownFonts.getStandardFamily())).colspan(1).right()
            }
        }
        this.name = "itemDetails"
        this.pack()
        this.width = 360f
    }
}