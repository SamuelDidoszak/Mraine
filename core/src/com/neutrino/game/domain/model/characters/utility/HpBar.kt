package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import space.earlygrey.shapedrawer.ShapeDrawer

class HpBar(
    var currentHp: Float,
    val maxHp: Float
): Actor() {
    val textureRegion: TextureRegion = TextureRegion(Texture("whitePixel.png"), 0, 0, 1, 1)
    init {
        name = "hpBar"
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val drawer = ShapeDrawer(batch, textureRegion)

        drawer.filledRectangle(2f, -24f, 60f * (currentHp / maxHp), 8f, color())
    }

    private fun color(): Color {
        val red = if (currentHp / maxHp >= 0.5f) 1 - (currentHp / maxHp) else 1f
        val green = if (currentHp / maxHp >= 0.5f) 1f else 2 * (currentHp / maxHp)

        return Color(red, green, 0f, 1f)
    }
}