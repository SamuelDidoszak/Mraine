package com.neutrino.game.domain.model.items.items

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType
import kotlin.math.roundToInt

class Gold: Item(), ItemType.MISC {
    override val name: String = "Gold"
    override val description: String = ""
    override var amount: Int? = 1
        set(value) {
            field = value
            texture = pickTexture()
            goldValueOg = amount!!
            goldValue = amount!!
            realValue = amount!!
        }

    override val textureNames: List<String> = listOf(
        "gold1", "gold2", "gold3", "gold4", "gold5", "gold6", "gold7", "gold8"
    )
    override var texture: TextureAtlas.AtlasRegion = pickTexture()
    override val itemTier: Int = 1

    override fun randomize(quality: Float, difficulty: Float): Item {
        val randomAmount =
            Constants.RandomGenerator.nextFloat() * (difficulty * 5) * quality
        amount = randomAmount.roundToInt()
        if (amount == 0)
            amount = 1

        return this
    }

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