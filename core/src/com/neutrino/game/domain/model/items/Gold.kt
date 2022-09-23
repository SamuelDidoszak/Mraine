package com.neutrino.game.domain.model.items

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants.RandomGenerator

class Gold(amount: Int = RandomGenerator.nextInt(120)): Item(), ItemType.MISC {
    override val name: String = "Gold"
    override val description: String = "Money"
    override var amount: Int? = amount
        set(value) {
            field = value
            texture = pickTexture()
        }
    override val stackable: Boolean = true

    override val textureNames: List<String> = listOf(
        "gold1", "gold2", "gold3", "gold4", "gold5", "gold6", "gold7", "gold8"
    )
    override var texture: TextureAtlas.AtlasRegion = pickTexture()

    private fun pickTexture(): TextureAtlas.AtlasRegion {
        val textureName =
             if (amount!! < 10) "gold1"
        else if (amount!! < 20) "gold2"
        else if (amount!! < 50) "gold3"
        else if (amount!! < 100) "gold4"
        else if (amount!! < 200) "gold5"
        else if (amount!! < 400) "gold6"
        else if (amount!! < 700) "gold7"
        else "gold8"

        return getTexture(textureName)
    }
}