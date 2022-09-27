package com.neutrino.game.domain.model.items.items

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.turn.Event

class BrokenStopwatch: Item(), ItemType.CAUSESEVENT {
    override val name: String = "Broken stopwatch"
    override val description: String = "This weird stopwatch looks broken yet powerful at the same time"
    override val stackable: Boolean = false
    override val textureNames: List<String> = listOf("brokenStopwatch")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    val effectLength: Double = 10.0

    override fun use(character: Character, turn: Double): Event {
        // TODO after adding a posibility to pass an eventList, add MODIFYSTAT for attackSpeed too
        return Event.MODIFYSTAT(character, "movementSpeed", -1 * character.movementSpeed, 0.0, 0.0, effectLength.toInt())
    }
}