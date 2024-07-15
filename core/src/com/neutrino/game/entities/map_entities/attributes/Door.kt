package com.neutrino.game.entities.map_entities.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map_entities.callables.InteractedCallable
import com.neutrino.game.entities.map_entities.util.Interactable
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.map.chunk.ChunkManager

class Door: Attribute(), Interactable {

    override var requiredDistance: Int = 1
    override var turnCost: Double = 1.0
    override var isPrimary: Boolean = true
    var open = false
    override fun interact() {
        open = !open
        entity.get(MapParams::class)!!.allowOnTop = open
        entity.get(MapParams::class)!!.allowCharacterOnTop = open
        // TODO check textures
        val textureName = if (open) entity.get(Texture::class)!!.textures[0].texture.name.substringBefore("Closed")
        else entity.get(Texture::class)!!.textures[0].texture.name.plus("Closed")
        entity.get(Texture::class)!!.textures[0] = Textures.get(textureName)
        isPrimary = !open

        if (entity.get(MapParams::class)?.allowCharacterOnTop == true)
            ChunkManager.characterMethods.removeImpassable(
                entity.get(Position::class)!!)
        else
            ChunkManager.characterMethods.addImpassable(
                entity.get(Position::class)!!)

        entity.call(InteractedCallable::class, this)
    }

    override fun onEntityAttached() {
        entity.addAttribute(Identity.Door())
        entity.addAttribute(ChangesImpassable())
        entity.addAttribute(MapParams(false, false))
    }
}