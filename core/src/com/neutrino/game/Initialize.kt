package com.neutrino.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.domain.model.map.Level
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class Initialize {

    val level: Level = Level(
        "test level",
        0,
        0,
        0,
        "A level for testing map generation",
        30,
        30
    )

    fun initialize() {
        level.provideTextures()
    }

    fun oldStuff() {
        val texture = Texture("environment/entities.png")
        println("texture value: $texture")

        val atlas = TextureAtlas("environment/tiles.atlas")
        for (texture in atlas.textures) {
            println(texture.toString())
            println(texture.toString().substring(0, texture.toString().lastIndexOf(".")) + ".atlas")
        }
        val textureRegion: TextureRegion = atlas.regions[0]
        for (region in atlas.regions)
            println(region.name)

        val textureList: List<TextureAtlas.AtlasRegion> = listOf()
        println(textureList.isEmpty())
    }
}