package com.neutrino.game.entities.map_entities.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.DefensiveStats
import com.neutrino.game.entities.characters.callables.attack.EntityDiedCallable
import com.neutrino.game.entities.characters.callables.attack.GotAttackedAfterCallable
import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.entities.util.Equality
import com.neutrino.game.graphics.drawing.layers.HpBar
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.map.chunk.ChunkManager

/**
 * @param destroyedTextureName if null, new texture will be the current texture with "Destroyed" suffix
 */
class Destructable(
    hp: Float,
    private var destroyedTextureName: String? = null,
    defence: Float = 0f,
    fireDefence: Float = 0f,
    waterDefence: Float = 0f,
    airDefence: Float = 0f,
    poisonDefence: Float = 0f,
    evasion: Float = 0f
): Attribute(), Equality<Destructable>, Cloneable<Destructable> {

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
        entity.addAttribute(ChangesImpassable())
        entity.attach(DestroyedCallable())
        entity.attach(object : GotAttackedAfterCallable() {
            override fun call(entity: Entity, vararg data: Any?): Boolean {
                if (entity hasNot HpBar::class) {
                    val hpBar = HpBar()
                    hpBar.xOffset = entity.get(Texture::class)!!.getWidthScaled().toFloat() - 64f
                    hpBar.yOffset = entity.get(Texture::class)!!.getHeightScaled().toFloat()
                    entity.addAttribute(hpBar)
                    hpBar.attach()
                }
                return true
            }
        })
    }

    private inner class DestroyedCallable: EntityDiedCallable() {

        override fun call(entity: Entity, vararg data: Any?): Boolean {
            entity.get(MapParams::class)?.allowOnTop = true
            entity.get(MapParams::class)?.allowCharacterOnTop = true
            entity.removeAttribute(DefensiveStats::class)
            entity.removeAttribute(HpBar::class)
            val texture = entity get Texture::class
            if (texture != null) {
                if (destroyedTextureName != null) {
                    texture.textures.clear()
                    texture.textures.add(Textures.get(destroyedTextureName!!))
                } else {
                    for (i in 0 until texture.textures.size) {
                        texture.textures[i] = Textures.get(texture.textures[i].texture.name + "Destroyed")
                    }
                }
            }
            ChunkManager.characterMethods.removeImpassable(entity.get(Position::class)!!)
            return true
        }
    }

    override fun clone(): Destructable {
        return Destructable(
            defensiveStats.hp,
            destroyedTextureName,
            defensiveStats.defence,
            defensiveStats.fireDefence,
            defensiveStats.waterDefence,
            defensiveStats.airDefence,
            defensiveStats.poisonDefence,
            defensiveStats.evasion
        )
    }
    override fun isEqual(other: Destructable): Boolean = entity.get(DefensiveStats::class)!!.isEqual(other.entity.get(DefensiveStats::class)!!)
}