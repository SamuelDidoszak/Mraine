package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.neutrino.game.util.Constants
import com.neutrino.game.graphics.utility.ColorUtils
import space.earlygrey.shapedrawer.ShapeDrawer

class HpBar(
    private var currentHp: Float,
    private val maxHp: Float
): Actor() {
    private val textureRegion: TextureRegion = TextureRegion(Constants.WhitePixel, 0, 0, 1, 1)
    private var drawer: ShapeDrawer? = null
    init {
        name = "hpBar"
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (drawer == null) {
            drawer = ShapeDrawer(batch, textureRegion)
            drawer!!.setColor(color())
        }

        drawer!!.filledRectangle(2f, -24f, 60f * (currentHp / maxHp), 8f)
    }

    /** Updates the bar color. Needed to avoid shapeDrawer creating hundreds of unreferenced color objects */
    fun update(currentHp: Float) {
        this.currentHp = currentHp
        drawer?.setColor(color())
    }

    private fun color(): Color {
        val red = if (currentHp / maxHp >= 0.5f) 1 - (currentHp / maxHp) else 1f
        val green = if (currentHp / maxHp >= 0.5f) 1f else 2 * (currentHp / maxHp)

        return ColorUtils.applySaturation(Color(red, green, 0f, 1f), 0.8f)
    }

    override fun remove(): Boolean {
        textureRegion.texture.dispose()
        return super.remove()
    }
}