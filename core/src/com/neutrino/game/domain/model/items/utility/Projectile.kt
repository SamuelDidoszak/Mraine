package com.neutrino.game.domain.model.items.utility

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.utility.VectorOperations


class Projectile(
    var sourceX: Float,
    var sourceY: Float,
    var destX: Float,
    var destY: Float,
    val projectileType: ProjectileType
): Actor(), TextureHaver {
    /**
     * @param speed tiles per second
     */
    enum class ProjectileType(val textureName: String, val speed: Float) {
        WOODENARROW("woodenArrow", 20f),
        FIREPROJECTILE("fireProjectile", 15f),
        POISONPROJECTILE("poisonProjectile", 15f),
        ROCK("rock", 12f),
    }

    /**
     * Casts a projectile from the center of the source tile to the center of the destination tile
     */
    constructor(sourceX: Int, sourceY: Int, destX: Int, destY: Int, projectileType: ProjectileType): this(
        sourceX * 64f - 32f,
        sourceY * 64f - 32f,
        destX * 64f - 32f,
        destY * 64f - 32f,
        projectileType
    )

    var flightTime: Float? = null

    override val textureNames: List<String> = ProjectileType.values().map { it.textureName }
    override var texture: TextureAtlas.AtlasRegion = getTexture(projectileType.textureName)
    override var mirrored: Boolean = false
    val textureHaver: TextureHaver = this

    override fun getTexture(name: String): TextureAtlas.AtlasRegion {
        try {
            return Constants.DefaultProjectileTexture.findRegion(name)
        } catch (e: NullPointerException) {
            println("TextureName:\t$name\tdoesn't exist")
            // Default texture
            return Constants.DefaultProjectileTexture.findRegion(textureNames[0])
        }
    }

    /**
     * Initializes and starts projectile. Has to have a parent first
     */
    fun init() {
        if (this.parent == null)
            return
        sourceY = Constants.LevelChunkSize * 64f - sourceY
        destY = Constants.LevelChunkSize * 64f - destY
        this.setPosition(sourceX, sourceY)
//        this.setOrigin(width, height * 2f)
        val distance = VectorOperations.getDistance(sourceX, sourceY, destX, destY)
        flightTime = (distance / 64) / projectileType.speed
        this.addAction(Actions.rotateBy(VectorOperations.pointsAngleDegrees(sourceX, sourceY, destX, destY)))
        this.addAction(
            Actions.sequence(
                Actions.moveBy(destX - sourceX, destY - sourceY, flightTime!!),
                Actions.fadeOut(0.5f),
                Actions.removeActor()
            )
        )
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        batch?.draw(texture, x, y, texture.regionWidth * 4f, texture.regionHeight * 2f,
            texture.regionWidth * 4f, texture.regionHeight * 4f,
            scaleX, scaleY, rotation)

        batch?.setColor(color.r, color.g, color.b, 1f)
    }
}