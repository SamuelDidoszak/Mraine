package com.neutrino.game.entities.map.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.DefensiveStats
import com.neutrino.game.entities.characters.callables.attack.EntityDiedCallable
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.map.chunk.ChunkManager

class Destructable(
    hp: Float,
    private var destroyedTextureName: String? = null,
    defence: Float = 0f,
    fireDefence: Float = 0f,
    waterDefence: Float = 0f,
    airDefence: Float = 0f,
    poisonDefence: Float = 0f,
    evasion: Float = 0f
): Attribute() {

    private val defensiveStats = DefensiveStats(
        hpMax = hp,
        defence = defence,
        fireDefence = fireDefence,
        waterDefence = waterDefence,
        airDefence = airDefence,
        poisonDefence = poisonDefence,
        evasion = evasion
    )

    override fun onEntityAttached() {
        entity.addAttribute(defensiveStats)
        entity.attach(DestroyedCallable())
    }

    private inner class DestroyedCallable: EntityDiedCallable() {

        override fun call(entity: Entity, vararg data: Any?): Boolean {
            entity.get(MapParams::class)?.allowOnTop = true
            entity.get(MapParams::class)?.allowCharacterOnTop = true
            val texture = entity get Texture::class
            if (texture != null) {
                destroyedTextureName = destroyedTextureName ?: (texture.textures[0].texture.name + "Destroyed")
                texture.textures[0] = Textures.get(destroyedTextureName!!)
            }
            ChunkManager.characterMethods.removeImpassable(entity.get(Position::class)!!)
            return true
        }
    }
}