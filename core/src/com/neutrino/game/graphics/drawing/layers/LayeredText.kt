package com.neutrino.game.graphics.drawing.layers

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel

open class LayeredText(
    text: String = "",
    centered: Boolean = true,
    width: Int = 0
): LayeredDraw() {

    var text = TextraLabel(text, KnownFonts.getStandardFamily())
    var centered: Boolean = centered
        set(value) {
            field = value
            centerText()
        }
    override var width: Int = width
        set(value) {
            field = value
            text.width = width.toFloat()
        }
    override var height: Int
        get() = text.prefHeight.toInt()
        set(value) {}
    init {
        if (width == 0)
            this.text.width = this.text.prefWidth
        else
            this.width = width
        this.text.wrap = true
        centerText()
    }

    override fun draw(batch: Batch, x: Float, y: Float, parentAlpha: Float) {
        text.x = x + getX()
        text.y = y + getY()
        text.draw(batch, parentAlpha * alpha)
    }

    private fun centerText() {
        if (centered)
            text.align = Align.center
        else
            text.align = Align.left
    }
}