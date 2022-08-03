package com.neutrino.game.domain.model.entities.utility

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion

abstract class Entity (

) {
    abstract val id: Int
    abstract val name: String
    open val description: String? = ""
    abstract var textureSrc: String
    abstract val textureNames: List<String>
    open var textureList: List<TextureAtlas.AtlasRegion> = listOf()
    abstract var texture: TextureRegion
    abstract val allowOnTop: Boolean
    abstract val allowCharacterOnTop: Boolean

    fun loadTextures(atlas: TextureAtlas) {
        textureList = buildList {
            for (name in textureNames) {
                add(atlas.findRegion(name))
            } }

    }

    abstract fun pickTexture(onMapPosition: OnMapPosition)

    fun getTexture(name: String): TextureAtlas.AtlasRegion {
        return textureList.find { it.name.toString() == name }!!
    }

    fun checkTile(onMapPosition: OnMapPosition, directionList: List<Int>): Entity? {
        var x = onMapPosition.xPos
        var y = onMapPosition.yPos

        for (i in directionList) {
            when(i) {
                1 -> {
                    x -= 1
                    y += 1
                } 2 -> {
                    y += 1
                } 3 -> {
                    x += 1
                    y += 1
                } 4 -> {
                    x -= 1
                } 5 -> {
                    // the same tile
                } 6 -> {
                    x += 1
                } 7 -> {
                    x -= 1
                    y -= 1
                } 8 -> {
                    y -= 1
                } 9 -> {
                    x += 1
                    y -= 1
                }
            }
        }
        if(y >= 0 && y < onMapPosition.map.size) {
            if(x >= 0 && x < onMapPosition.map[y].size) {
                return onMapPosition.map[y][x][0]
            }
        }
        return null
    }
}