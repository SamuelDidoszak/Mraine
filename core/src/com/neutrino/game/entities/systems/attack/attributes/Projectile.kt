package com.neutrino.game.entities.systems.attack.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.util.AttributeOperations
import com.neutrino.game.graphics.drawing.actions.Action
import com.neutrino.game.graphics.drawing.drawables.RotatingDrawableTexture
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.util.Constants
import com.neutrino.game.util.VectorOperations
import com.neutrino.game.util.position
import kotlin.math.abs

class Projectile(
    val projectileType: ProjectileType
): Attribute(), AttributeOperations<Projectile> {

    enum class ProjectileType(val textureName: String,
                              val speed: Float,
                              val centerOnEntity: Boolean,
                              val offsetX: Float = 0f, val offsetY: Float = 0f,
                              val waitTime: Float = 0f) {
        WOODENARROW("woodenArrow", 20f, true),
        FIREPROJECTILE("fireProjectile", 15f, false, 54f, 50f, 0.6f),
        POISONPROJECTILE("poisonProjectile", 15f, true)
    }

    fun shoot(entity: Entity) {
        shoot(entity.get(Position::class)!!,
            entity.get(Texture::class)!!.getWidthScaled() / 2f,
            entity.get(Texture::class)!!.getHeightScaled()/ 2f)
    }

    fun shoot(position: Position, destXOffset: Float = 32f, destYOffset: Float = 32f) {
        // TODO CHUNKS Implement positional differences for different chunks
        var sourceX = entity.position.x * 64f
        var sourceY = Constants.LevelChunkSize * 64f - entity.position.y * 64f + projectileType.offsetY
        var destX = position.x * 64f + destXOffset
        var destY = Constants.LevelChunkSize * 64f - position.y * 64f + destYOffset

        sourceX +=
            if (entity.get(Texture::class)?.textures?.isMirrored() == true && projectileType.offsetX != 0f)
                entity.get(Texture::class)!!.getWidthScaled() - projectileType.offsetX
            else
                projectileType.offsetX

        if (projectileType.centerOnEntity) {
            sourceX += entity.get(Texture::class)!!.getWidthScaled() / 2
            sourceY += entity.get(Texture::class)!!.getHeightScaled() / 2
        }

        val projectileEntity = Entity().addAttribute(entity.position.clone()).addAttribute(DrawPosition())
        val projectile = RotatingDrawableTexture(projectileEntity, Textures.get(projectileType.textureName))
        val rotation = VectorOperations.pointsAngleDegrees(sourceX, sourceY, destX, destY)
        projectile.setRotation(0f, 0.5f, rotation)
        sourceY -= projectile.height / 2f

        projectileEntity.get(DrawPosition::class)!!.x = sourceX
        projectileEntity.get(DrawPosition::class)!!.y = sourceY
        projectile.initialize(projectileEntity)

        val rotation360 = (if (rotation > 0) rotation else 360 + rotation) % 360
        val triangle = (1 / 90f) * (90 - abs((rotation360 % 180) - 90))
        var xOffset = 0f
        var yOffset = 0f

        when (rotation360.toInt()) {
            in 0 .. 90 -> {
                xOffset = -1 * (1 - triangle) * projectile.width
                yOffset = -1 * triangle * projectile.width
            } in 90 .. 180 -> {
                xOffset = (1 - triangle) * projectile.width
                yOffset = -1 * triangle * projectile.width
            } in 180 .. 270 -> {
                xOffset = (1 - triangle) * projectile.width
                yOffset = triangle * projectile.width
            } in 270 .. 360 -> {
                xOffset = -1 * (1 - triangle) * projectile.width
                yOffset = triangle * projectile.width
            }
        }

        destX += xOffset
        destY += yOffset

        val distance = VectorOperations.getDistance(sourceX, sourceY, destX, destY)
        val flightTime = (distance / 64) / projectileType.speed

        projectile.addAction(
            Action.Sequence(
                Action.Wait(projectileType.waitTime),
                Action.MoveBy(destX - sourceX, destY - sourceY, flightTime),
                Action.FadeOut(2.5f),
                Action.Delete()
            )
        )
    }

    override fun plusEquals(other: Projectile) = plusPrevious(other)
    override fun minusEquals(other: Projectile) = minusPrevious(other)
    override fun clone(): Projectile = Projectile(projectileType)
    override fun isEqual(other: Projectile): Boolean = projectileType == other.projectileType
}