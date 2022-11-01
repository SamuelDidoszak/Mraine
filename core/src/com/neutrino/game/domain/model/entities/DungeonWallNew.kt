package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.EntityChecker
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class DungeonWallNew: Entity() {
    override val allowOnTop = false
    override val allowCharacterOnTop = false
    override val name = "Dungeon wall"
    override val description = "The wall of a dungeon. It wouldn't be fun if it suddenly collapsed"

    override val textureNames: List<String> = listOf("dungeonWall1","dungeonWall2","dungeonWall3","dungeonWall4","dungeonWall5",
        "dungeonWallLeft1","dungeonWallLeft2","dungeonWallLeft3","dungeonWallLeft4","dungeonWallLeft5","dungeonWallLeftBottom","dungeonWallLeftTop",
        "dungeonWallRight1","dungeonWallRight2","dungeonWallRight3","dungeonWallRight4","dungeonWallRight5","dungeonWallRightBottom","dungeonWallRightTop",
        "dungeonWallThickBottom","dungeonWallThickInside1","dungeonWallThickInside2","dungeonWallThickInside3","dungeonWallThickTop",
        "dungeonWallInside"
        )
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition) {
        val randVal = Constants.RandomGenerator.nextFloat() * 100

        val entityChecker = EntityChecker(onMapPosition, "DungeonWall", skipList = listOf(7, 9))

        val defaultWall = getTextureFromEqualRange(randVal, until = 90f, textures = textureNames.subList(0, 4)) ?:
        getTextureFromEqualRange(randVal, 90f, until = 98f, textures = textureNames.subList(4, 6)) ?:
        getTextureFromEqualRange(randVal, 98f, until = 100f, textures = textureNames.subList(6, 10)) ?:
        textureNames[0]

        val textureName: String =
//            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 4, 6, 7, 8, 9))) "wallInBetween" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 8))) "wallVerticalEmptyLeftRight" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 8))) "wallVerticalEmptyLeft" else
            if (entityChecker.checkAllTiles(listOf(2, 3, 8))) "wallVerticalEmptyRight" else
            if (entityChecker.checkAllTiles(listOf(2, 4, 6), tileSkipList = listOf(1, 3))) "wallTSection" else
            if (entityChecker.checkAllTiles(listOf(2 ,8, 6), tileSkipList = listOf(1, 3))) "wallVertical" else
            if (entityChecker.checkAllTiles(listOf(2 ,8, 4), tileSkipList = listOf(1, 3))) "wallVertical" else
            if (entityChecker.checkAllTiles(listOf(2 ,8), tileSkipList = listOf(1, 3))) "wallVertical" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 4))) "wallVerticalEmptyLeftRightClosed" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 6))) "wallVerticalEmptyLeftRightClosed" else
            if (entityChecker.checkAllTiles(listOf(4, 2, -7))) "wallVerticalEndUp" else
            if (entityChecker.checkAllTiles(listOf(2, 6, 9))) "wallVerticalEndUp" else
            if (entityChecker.checkAllTiles(listOf(4, 2, 7, 3))) "wallVerticalEmptyRightClosed" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 4))) "wallVerticalEdgeUpLeft" else
            if (entityChecker.checkAllTiles(listOf(2, 3, 6))) "wallVerticalEdgeUpRight" else
            if (entityChecker.checkAllTiles(listOf(2, 4))) "wallVerticalEdgeUpLeft" else
            if (entityChecker.checkAllTiles(listOf(2, 6))) "wallVerticalEdgeUpRight" else
            if (entityChecker.checkAllTiles(listOf(4, 6), tileSkipList = listOf(1, 3)))"wallHorizontalMiddle" else
            if (entityChecker.checkAllTiles(listOf(6, 7), tileSkipList = listOf(1, 2, 3)))"wallHorizontalMiddleLeft" else
            if (entityChecker.checkAllTiles(listOf(8), tileSkipList = listOf(1, 3))) defaultWall else
            if (entityChecker.checkAllTiles(listOf(2), tileSkipList = listOf(1, 3)))"wallVerticalEndUp" else
            if (entityChecker.checkAllTiles(listOf(4), tileSkipList = listOf(1, 2, 3)))"wallHorizontalMiddleRight" else
            if (entityChecker.checkAllTiles(listOf(6), tileSkipList = listOf(1, 2, 3)))"wallHorizontalMiddleLeft" else
            if (entityChecker.checkAllTiles(listOf(), tileSkipList = listOf(1, 3)))"singleWall" else
                defaultWall

        texture = getTexture(textureName)
    }

    override fun getTexture(name: String): TextureAtlas.AtlasRegion {
        return Constants.DefaultEntityNewTexture.findRegion(name)
    }
}