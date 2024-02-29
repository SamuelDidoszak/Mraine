package com.neutrino.game.UI.utility

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.items.attributes.Amount
import com.neutrino.game.entities.items.attributes.GoldValue
import com.neutrino.game.entities.shared.attributes.DrawerAttribute
import com.neutrino.game.graphics.drawing.SingleEntityDrawer
import com.neutrino.game.util.Constants

class EqActor(val entity: Entity): Group(), PickupActor {
    // needed for resize
    override val ogWidth = 64f
    override val ogHeight = 64f
    private var actorWidth: Float = ogWidth
    private var actorHeight: Float = ogHeight
    private lateinit var numberText: TextraLabel

    var amount: Int
        get() = entity.get(Amount::class)!!.amount
        set(value) { entity.get(Amount::class)!!.amount = value }

    val maxStack: Int
        get() = entity.get(Amount::class)!!.maxStack

    init {
        addActor(SingleEntityDrawer(entity, false))
        children[0].setSize(64f, 64f)
        children[0].setPosition(8f, 8f)

        val comparedValue = entity.get(GoldValue::class)?.compareToOriginal()
        if (comparedValue != null && comparedValue != 0) {
            val qualityImage =
            if (comparedValue > 0)
                Image(Constants.DefaultIconTexture.findRegion("itemBetter2"))
            else
                Image(Constants.DefaultIconTexture.findRegion("itemWorse"))
            qualityImage.name = "itemQuality"
            addActor(qualityImage)
            qualityImage.align = Align.bottomLeft
            qualityImage.y += 4
        }

        if (entity.get(Amount::class)!!.maxStack > 1) {
            numberText = TextraLabel("[#121212ff][@Cozette][%600][*]$amount", KnownFonts.getStandardFamily())
            numberText.name = "amount"
            numberText.setBounds(0f, 0f, 72f, 24f)
            numberText.align = Align.right
            addActor(numberText)
            numberText.x += 5
            numberText.y += 4
        }
        name = entity.name
    }
    override fun draw(batch: Batch?, parentAlpha: Float) {
        // required for fading
        val color: com.badlogic.gdx.graphics.Color = color
        batch?.setColor(color.r, color.g, color.b, color.a * parentAlpha)

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
        numberText.setText("[#121212ff][@Cozette][%600][*]" + entity.get(Amount::class)!!.amount.toString())
        numberText.setBounds(0f, 0f, 72f, 24f)
        numberText.align = Align.right
    }

    override fun remove(): Boolean {
        entity.removeAttribute(DrawerAttribute::class)
        return super.remove()
    }
}