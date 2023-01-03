package com.neutrino.game.domain.model.items.utility

import com.badlogic.gdx.scenes.scene2d.Group

interface HasProjectile {
    val projectileType: Projectile.ProjectileType

    /**
     * Initializes the projectile, add it to the group and shoots
     * @return returns the projectile flight time
     */
    fun shoot(sourceX: Float,
              sourceY: Float,
              destX: Float,
              destY: Float,
              group: Group): Float {
        val projectile = Projectile(sourceX, sourceY, destX, destY, projectileType)
        group.addActor(projectile)
        projectile.init()
        return projectile.flightTime!!
    }


    /**
     * Initializes the projectile, add it to the group and shoots
     * @return returns the projectile flight time
     */
    fun shoot(sourceX: Int,
              sourceY: Int,
              destX: Int,
              destY: Int,
              group: Group): Float {
        val projectile = Projectile(sourceX, sourceY, destX, destY, projectileType)
        group.addActor(projectile)
        projectile.init()
        return projectile.flightTime!!
    }
}