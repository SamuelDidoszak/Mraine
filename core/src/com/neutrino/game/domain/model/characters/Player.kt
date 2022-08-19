package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.DefaultTextures
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.characters.utility.Stats
import com.neutrino.game.domain.model.entities.utility.TextureHaver

object Player : Character(0, 0), Animated {
    override var hp: Float = 10f
    override var currentHp: Float = hp
    override var mp: Float = 10f
    override var currentMp: Float = mp
    override var strength: Float = 2f
    override var defence: Float = 2f
    override var agility: Float = 2f
    override var evasiveness: Float = 2f
    override var accuracy: Float = 2f
    override var criticalChance: Float = 0.3f
    override var luck: Float = 2f
    override var attackSpeed: Float = 2f
    override var movementSpeed: Float = 1f

    init {
        setName("Player")
    }

    override val description: String
        get() = TODO("Not yet implemented")

    override var textureSrc: String = "characters/player.png"
    override val textureNames: List<String> = listOf(
        "buddy#1", "buddy#2", "buddy#3", "buddy#4", "buddy#5"
    )
    override var textureList: List<TextureAtlas.AtlasRegion> = listOf()
    override var texture: TextureAtlas.AtlasRegion = if(textureList.isNotEmpty()) textureList[0] else TextureAtlas.AtlasRegion(DefaultTextures[6][5])

    override val textureHaver: TextureHaver = this

    override var animation: Animation<TextureRegion>? = null

}