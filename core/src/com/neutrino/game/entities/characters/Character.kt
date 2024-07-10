package com.neutrino.game.entities.characters

import com.neutrino.game.entities.Characters
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.textures.AnimatedTextureSprite
import com.neutrino.game.graphics.textures.Textures
import java.util.*

class Character: Entity() {

    override var name: String = ""
        get() {return if (nameSet) field else Characters.getName(id)}
        set(value) {
            nameSet = true
            field = value
        }

    fun setAnimation(type: String, next: String? = null, remainMirrored: Boolean = true) {
        fun getAnimation(name: String): AnimatedTextureSprite {
            val animation = Textures.getOrNull(
                (if (this == Player) "player" else Characters.getName(id).lowercase()) +
                        name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                ) as AnimatedTextureSprite?
            if (animation == null)
                return get(Texture::class)!!.textures[0] as AnimatedTextureSprite
            if (remainMirrored)
                animation.mirrorX = get(Texture::class)!!.textures.isMirrored()
            animation.y += 5f
            return animation
        }

        get(Texture::class)!!.setAnimation(
            getAnimation(type),
            if (next == null) null else getAnimation(next)
        )
    }
}