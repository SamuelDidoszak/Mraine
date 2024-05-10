package com.neutrino.game.graphics.drawing.layers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.callables.StatsChangedCallable
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.Constants
import space.earlygrey.shapedrawer.ShapeDrawer

class HpBar: LayeredDraw() {

    private lateinit var stats: DefensiveStats
    override fun onEntityAttached() {
        stats = entity.get(DefensiveStats::class)!!
        height = 8
        width = 64
        entity.attach(object : StatsChangedCallable() {
            override fun call(entity: Entity, vararg data: Any?) {
                entity.get(HpBar::class)?.updateColor()
            }
        })
    }

    private val textureRegion: TextureRegion = TextureRegion(Constants.WhitePixel, 0, 0, 1, 1)
    private var drawer: ShapeDrawer? = null

    override fun draw(batch: Batch, x: Float, y: Float, parentAlpha: Float) {
        if (drawer == null) {
            drawer = ShapeDrawer(batch, textureRegion)
            drawer!!.setColor(color())
        }

        batch.setAlpha(parentAlpha * alpha)
        drawer!!.filledRectangle(x + getX() + 2f, y + getY(), 60f * (stats.hp / stats.hpMax), 8f)
    }

    /** Updates the bar color. Needed to avoid shapeDrawer creating hundreds of unreferenced color objects */
    fun updateColor() = drawer?.setColor(color())

    private fun color(): Color {
        val red = if (stats.hp / stats.hpMax >= 0.5f) 1 - (stats.hp / stats.hpMax) else 1f
        val green = if (stats.hp / stats.hpMax >= 0.5f) 1f else 2 * (stats.hp / stats.hpMax)

        return ColorUtils.applySaturation(Color(red, green, 0f, 1f), 0.8f)
    }
}