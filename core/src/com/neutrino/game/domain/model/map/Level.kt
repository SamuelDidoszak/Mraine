package com.neutrino.game.domain.model.map

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.LevelChunkSize
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.domain.use_case.map.GetMap
import com.neutrino.game.domain.use_case.map.MapUseCases

val mapUsecases = MapUseCases(GetMap())

class Level(
    val name: String,
    val xIndex: Int,
    val yIndex: Int,
    val zIndex: Int,
    val description: String?,
    val sizeX: Int = LevelChunkSize,
    val sizeY: Int = LevelChunkSize,
) {
    val id: Int = "$xIndex-$yIndex-$zIndex".hashCode()
    val textureList: ArrayList<TextureAtlas> = ArrayList()

    val map: Map = Map(id,
        name = "$name $zIndex",
        xMax = sizeX,
        yMax = sizeY,
        map = mapUsecases.getMap(this)
    )

    /**
     * Make it a ObjectSet or OrderedSet / OrderedMap for fast read / write / delete
     */
    val characterMap: CharacterMap? = null

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

}
