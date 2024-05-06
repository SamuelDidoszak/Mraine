package com.neutrino.game.entities.characters

import com.neutrino.game.entities.Characters
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.textures.AnimatedTextureSprite
import com.neutrino.game.graphics.textures.Textures

class Character: Entity() {

    override var name: String = ""
        get() {return if (nameSet) field else Characters.getName(id)}
        set(value) {
            nameSet = true
            field = value
        }

    fun setAnimation(type: String, next: String? = null, remainMirrored: Boolean = false) {
        fun getAnimation(name: String): AnimatedTextureSprite {
            val animation =  Textures.get(
                (if (this == Player) "player" else Characters.getName(id).lowercase()) +
                        "_" + name.lowercase()) as AnimatedTextureSprite
            if (remainMirrored)
                animation.mirrorX = get(Texture::class)!!.textures.isMirrored()
            return animation
        }

        get(Texture::class)!!.setAnimation(
            getAnimation(type),
            if (next == null) null else getAnimation(next)
        )
    }
}