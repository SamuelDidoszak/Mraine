package com.neutrino.game.domain.model.items.equipment

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.domain.model.items.Item

class EqActor(val item: Item): Group() {
    init {
        if (item.amount != null) {
            val numberText = TextraLabel(item.amount.toString(), KnownFonts.getStandardFamily())
            numberText.name = "amount"
            addActor(numberText)
        }
        name = item.name
    }
    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.draw(item.texture, this.x, this.y, item.texture.regionWidth * 4f * 1.25f, item.texture.regionHeight * 4f * 1.25f)
        super.draw(batch, parentAlpha)
    }
}