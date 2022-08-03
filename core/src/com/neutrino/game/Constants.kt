package com.neutrino.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

const val AnimationSpeed: Float = 0.1666666666666666f
    //  Textures are provided here to omit the need of loading them multiple times
val DefaultTextures: Array<Array<TextureRegion>> = TextureRegion.split(Texture("environment/tiles.png"), 16, 16)

// Level constants

const val LevelChunkSize: Int = 100