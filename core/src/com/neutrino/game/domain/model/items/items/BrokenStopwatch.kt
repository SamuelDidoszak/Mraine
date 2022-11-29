package com.neutrino.game.domain.model.items.items

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.items.Item

class BrokenStopwatch: Item() {
    override val name: String = "Broken stopwatch"
    override val description: String = "This weird stopwatch looks broken yet powerful at the same time"
    override val textureNames: List<String> = listOf("brokenStopwatch")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override val itemTier: Int = 4

    val effectLength: Double = 10.0

    fun use(character: Character, turn: Double) {
//        return EventOld.MODIFYSTAT(character, "movementSpeed", -1 * character.movementSpeed, 0.0, 0.0, effectLength.toInt())
    }
}