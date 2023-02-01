package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.EntityChecker
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.domain.model.entities.utility.Wall
import kotlin.random.Random

class DungeonWall: Wall() {
    override var allowOnTop = false
    override var allowCharacterOnTop = false
    override val name = "Dungeon wall"
    override val description = "The wall of a dungeon. It wouldn't be fun if it suddenly collapsed"

    override val textureNames: List<String> = listOf("dungeonWall$1","dungeonWall$2","dungeonWall$3","dungeonWall$4","dungeonWall$5",
        "dungeonWallLeft$1","dungeonWallLeft$2","dungeonWallLeft$3","dungeonWallLeft$4","dungeonWallLeft$5",
        "dungeonWallRight$1","dungeonWallRight$2","dungeonWallRight$3","dungeonWallRight$4","dungeonWallRight$5",
        "dungeonWallInsideMiddle$1","dungeonWallInsideMiddle$2","dungeonWallInsideMiddle$3","dungeonWallInsideMiddle$4","dungeonWallInsideMiddle$5",
        "dungeonWallInside","dungeonWallInsideMiddleTop",
        "dungeonWallInsideEdgeLeft$1","dungeonWallInsideEdgeLeft$2","dungeonWallInsideEdgeRight$1","dungeonWallInsideEdgeRight$2",
        "dungeonWallInsideTop$1","dungeonWallInsideTop$2","dungeonWallInsideTop$3","dungeonWallInsideTop$4","dungeonWallInsideTop$5",
        "dungeonWallSingle$1","dungeonWallSingle$2",
        "dungeonWallSingleHorizontal$1","dungeonWallSingleHorizontal$2","dungeonWallSingleHorizontal$3","dungeonWallSingleHorizontal$4","dungeonWallSingleHorizontal$5",
        )
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        val randVal = randomGenerator.nextFloat() * 100

        val entityChecker = EntityChecker(onMapPosition, "DungeonWall", skipList = listOf(7, 9))

        val dungeonWall = getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("dungeonWall$") })!!
        val dungeonWallLeft = getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("dungeonWallLeft$") })!!
        val dungeonWallRight = getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("dungeonWallRight$") })!!
        val dungeonWallInsideTop = getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("dungeonWallInsideTop$") })!!
        val dungeonWallInsideMiddle = getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("dungeonWallInsideMiddle$") })!!
        val dungeonWallInsideEdgeLeft = getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("dungeonWallInsideEdgeLeft$") })!!
        val dungeonWallInsideEdgeRight = getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("dungeonWallInsideEdgeRight$") })!!
        val dungeonWallSingle = getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("dungeonWallSingle$") })!!
        val dungeonWallSingleHorizontal = getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("dungeonWallSingleHorizontal$") })!!

        // TODO add entityChecker 'oneOf' rule
        // TODO make an algorithm that compresses rules into only a few checks and saves the code to a file
        val textureName: String =
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 4, 6, 7, 8, 9))) "dungeonWallInside" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 4, 6, 8, 9))) "dungeonWallInside" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 4, 6, 7, 8))) "dungeonWallInside" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 4, 6, 8))) "dungeonWallInside" else

            if (entityChecker.checkAllTiles(listOf(2, 8, 1, 4, 7))) dungeonWallRight else
            if (entityChecker.checkAllTiles(listOf(2, 8, 1, 4, 7, 3))) dungeonWallRight else
            if (entityChecker.checkAllTiles(listOf(2, 8, 1, 4, 7, 6))) dungeonWallRight else
            if (entityChecker.checkAllTiles(listOf(2, 8, 1, 4, 7, 9))) dungeonWallRight else
            if (entityChecker.checkAllTiles(listOf(1, 2, 4, 8, 9))) dungeonWallRight else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 4, 8))) dungeonWallRight else
            if (entityChecker.checkAllTiles(listOf(1, 2, 4, 6, 8))) dungeonWallRight else
            if (entityChecker.checkAllTiles(listOf(1, 2, 4, 8))) dungeonWallRight else

            if (entityChecker.checkAllTiles(listOf(2, 8, 3, 6, 9))) dungeonWallLeft else
            if (entityChecker.checkAllTiles(listOf(2, 8, 3, 6, 9, 1))) dungeonWallLeft else
            if (entityChecker.checkAllTiles(listOf(2, 8, 3, 6, 9, 4))) dungeonWallLeft else
            if (entityChecker.checkAllTiles(listOf(2, 8, 3, 6, 9, 7))) dungeonWallLeft else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 6, 8))) dungeonWallLeft else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 4, 6, 7, 8))) dungeonWallLeft else
            if (entityChecker.checkAllTiles(listOf(2, 3, 4, 6, 7, 8))) dungeonWallLeft else
            if (entityChecker.checkAllTiles(listOf(2, 3, 6, 7, 8))) dungeonWallLeft else
            if (entityChecker.checkAllTiles(listOf(2, 3, 6, 8))) dungeonWallLeft else

            if (entityChecker.checkAllTiles(listOf(2, 8))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 8, 6, 9, 7))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 8, 6, 9))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(4, 7, 2, 8, 9))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(1, 2, 8, 9))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 3, 4, 7, 8, 9))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 4, 6, 7, 8, 9))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 3, 4, 7, 8))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 8))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 6, 7, 8))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(1, 2, 8))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 8, 4))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 8, 6))) dungeonWallInsideMiddle else

            if (entityChecker.checkAllTiles(listOf(1, 2, 3))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(2, 3, 4, 7))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(2, 3))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(1, 2))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(2))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 6, 9))) "dungeonWallInsideMiddleTop" else

            if (entityChecker.checkAllTiles(listOf(2, 4, 6, 9))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(2, 3, 4))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(2, 4, 6))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 6))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(2, 4, 7))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(2, 4, 6, 7))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(2, 6))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(2, 6, 9))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(2, 4))) "dungeonWallInsideMiddleTop" else
            if (entityChecker.checkAllTiles(listOf(2, 4, 6, 7, 8))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 3, 4, 6, 9))) dungeonWallInsideEdgeLeft else
            if (entityChecker.checkAllTiles(listOf(2, 3, 4, 6))) dungeonWallInsideEdgeLeft else
            if (entityChecker.checkAllTiles(listOf(1, 2, 4, 6, 7))) dungeonWallInsideEdgeRight else
            if (entityChecker.checkAllTiles(listOf(1, 2, 4, 6))) dungeonWallInsideEdgeRight else
            if (entityChecker.checkAllTiles(listOf(2, 4, 6, 8, 9))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 4, 7, 8))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 3, 4, 8))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 3, 8))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(1, 2, 6, 8, 9))) dungeonWallInsideMiddle else
            if (entityChecker.checkAllTiles(listOf(2, 3, 4, 6, 8))) dungeonWallLeft else

            if (entityChecker.checkAllTiles(listOf(4, 6))) dungeonWallSingleHorizontal else
            if (entityChecker.checkAllTiles(listOf(6, 9))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(1, 4, 3, 6, 9))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(1, 4))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(1, 4, 6))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(4))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(6))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(4, 3, 6, 9))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(3, 6))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(4, 6, 3))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(4, 7, 3, 6))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(1))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(7))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(9))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf(3))) dungeonWallSingle else
            if (entityChecker.checkAllTiles(listOf())) dungeonWallSingle else


            if (entityChecker.checkAllTiles(listOf(2, 4, 6), tileSkipList = listOf(1, 7, 3, 9))) dungeonWallInsideTop else
            if (entityChecker.checkAllTiles(listOf(2, 6), tileSkipList = listOf(1, 7, 3, 9))) dungeonWallInsideEdgeLeft else
            if (entityChecker.checkAllTiles(listOf(2, 4), tileSkipList = listOf(1, 7, 3, 9))) dungeonWallInsideEdgeRight else
            if (entityChecker.checkAllTiles(listOf(4, 2), tileSkipList = listOf(1, 7, 3, 9))) "dungeonWallRightTop" else
            if (entityChecker.checkAllTiles(listOf(6, 2), tileSkipList = listOf(1, 7, 3, 9))) "dungeonWallLeftTop" else
                dungeonWall

        texture = getTexture(textureName)
    }
}