package com.neutrino.game.UI.utility

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.items.Item

class EqActor(val item: Item): Group() {
    // needed for resize
    private val ogWidth = item.texture.regionWidth * 4f
    private val ogHeight = item.texture.regionWidth * 4f
    private var actorWidth: Float = ogWidth
    private var actorHeight: Float = ogHeight

    private lateinit var numberText: TextraLabel

    init {
        if (item.goldValueOg != 0 && item.goldValueOg != item.goldValue) {
            val qualityImage =
            if (item.goldValue > item.goldValueOg) {
                Image(Constants.DefaultIconTexture.findRegion("itemBetter2"))
            } else
                Image(Constants.DefaultIconTexture.findRegion("itemWorse"))
            qualityImage.name = "itemQuality"
            addActor(qualityImage)
            qualityImage.align = Align.bottomLeft
            qualityImage.y += 4
        }

        if (item.amount != null) {
            numberText = TextraLabel("[#121212ff][@Cozette][%600][*]" + item.amount.toString(), KnownFonts.getStandardFamily())
            numberText.name = "amount"
            numberText.setBounds(0f, 0f, 72f, 24f)
            numberText.align = Align.right
            addActor(numberText)
            numberText.x += 5
            numberText.y += 4
        }
        name = item.name
    }
    override fun draw(batch: Batch?, parentAlpha: Float) {
        // required for fading
        val color: com.badlogic.gdx.graphics.Color = color
        batch?.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        batch?.draw(item.texture, this.x + 8, this.y + 8, actorWidth, actorHeight)
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

    fun refreshAmount() {
        numberText.setText("[#121212ff][@Cozette][%600][*]" + item.amount.toString())
        numberText.setBounds(0f, 0f, 72f, 24f)
        numberText.align = Align.right
    }
}