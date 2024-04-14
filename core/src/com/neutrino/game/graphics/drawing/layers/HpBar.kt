package com.neutrino.game.graphics.drawing.layers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.entities.characters.attributes.DefensiveStats
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.Constants
import space.earlygrey.shapedrawer.ShapeDrawer

class HpBar: LayeredDraw() {

    private lateinit var stats: DefensiveStats
    override fun onEntityAttached() {
        stats = entity.get(DefensiveStats::class)!!
        height = 8
        width = 64
    }

    private val textureRegion: TextureRegion = TextureRegion(Constants.WhitePixel, 0, 0, 1, 1)
    private var drawer: ShapeDrawer? = null

    override fun draw(batch: Batch, x: Float, y: Float, alpha: Float) {
        if (drawer == null) {
            drawer = ShapeDrawer(batch, textureRegion)
            drawer!!.setColor(color())
        }

        drawer!!.filledRectangle(x + getX() + 2f, y + getY(), 60f * (stats.hp / stats.hpMax), 8f)
    }

    private fun color(): Color {
        val red = if (stats.hp / stats.hpMax >= 0.5f) 1 - (stats.hp / stats.hpMax) else 1f
        val green = if (stats.hp / stats.hpMax >= 0.5f) 1f else 2 * (stats.hp / stats.hpMax)

        return ColorUtils.applySaturation(Color(red, green, 0f, 1f), 0.8f)
    }
}