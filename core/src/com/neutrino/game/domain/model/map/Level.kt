package com.neutrino.game.domain.model.map

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.game.LevelChunkSize
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.domain.use_case.map.GetMap
import com.neutrino.game.domain.use_case.map.MapUseCases


class Level(
    name: String,
    val xIndex: Int,
    val yIndex: Int,
    zIndex: Int,
    val description: String?,
    val sizeX: Int = LevelChunkSize,
    val sizeY: Int = LevelChunkSize,
    val xScreen: Float = 0f,
    val yScreen: Float = 0f
): Group() {
    val mapUsecases = MapUseCases(this)

    val id: Int = "$xIndex-$yIndex-$zIndex".hashCode()
    val textureList: ArrayList<TextureAtlas> = ArrayList()

    val map: Map = Map(id,
        name = "$name $zIndex",
        xMax = sizeX,
        yMax = sizeY,
        map = mapUsecases.getMap()
    )

    init {
        setBounds(xScreen, yScreen, sizeX * 16f, sizeY * 16f)
    }

    // Make it a ObjectSet or OrderedSet / OrderedMap for fast read / write / delete
    val characterMap: MutableList<Character> = ArrayList()

    /**
     * Fills the level textureList with textures needed by the level
     * Provides the textures for every entity on the map
     */
    fun provideTextures() {
        println("called")
        for (y in 0 until map.yMax) {
            for (x in 0 until map.xMax) {
                for (z in 0 until map.map[y][x].size) {
                    var exists = false
                    val textureSrc = map.map[y][x][z].textureSrc

                    for (t in 0 until textureList.size) {
                        for (texture in textureList[t].textures) {
                            if (texture.toString() == textureSrc) {
                                exists = true
                                map.map[y][x][z].loadTextures(textureList[t])
                                map.map[y][x][z].pickTexture(OnMapPosition(map.map, x, y, z))
                                continue
                            }
                        }
                    }
                    if (!exists) {
                        textureList.add(TextureAtlas(textureSrc.substring(0, textureSrc.lastIndexOf(".")) + ".atlas"))
                        map.map[y][x][z].loadTextures(textureList[textureList.size - 1])
                        map.map[y][x][z].pickTexture(OnMapPosition(map.map, x, y, z))
                    }
                }
            }
        }
    }

    fun doesAllowCharacter(xPos: Int, yPos: Int): Boolean {
        var allow = true
        for (entity in map.map[yPos][xPos]) {
            if (!entity.allowCharacterOnTop) {
                allow = false
                break
            }
        }
        return allow
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        var screenX = 0f
        var screenY = height

        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                for (entity in map.map[y][x]) {
                    batch!!.draw(entity.texture, screenX, screenY)
                }
                screenX += 16
            }
            screenY -= 16
            screenX = 0f
        }
    }

}
