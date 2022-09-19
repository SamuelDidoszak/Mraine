package com.neutrino.game.domain.model.items.equipment

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.domain.model.items.Item

class EqActor(val item: Item): Group() {
    // needed for resize
    private val ogWidth = item.texture.regionWidth * 4f * 1.25f
    private val ogHeight = item.texture.regionWidth * 4f * 1.25f
    private var actorWidth: Float = ogWidth
    private var actorHeight: Float = ogHeight

    init {
        if (item.amount != null) {
            val numberText = TextraLabel(item.amount.toString(), KnownFonts.getStandardFamily())
            numberText.name = "amount"
            addActor(numberText)
            numberText.x += 32
            numberText.y += 8
        }
        name = item.name
    }
    override fun draw(batch: Batch?, parentAlpha: Float) {
        // required for fading
        val color: com.badlogic.gdx.graphics.Color = color
        batch?.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        batch?.draw(item.texture, this.x, this.y, actorWidth, actorHeight)
        super.draw(batch, parentAlpha)

        // required for fading
        color.a = 1f
        batch?.color = color
    }

    override fun setScale(scaleX: Float, scaleY: Float) {
        super.setScale(scaleX, scaleY)
        actorWidth = ogWidth * scaleX
        actorHeight = ogHeight * scaleY
    }
}