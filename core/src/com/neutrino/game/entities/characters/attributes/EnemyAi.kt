package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.domain.model.turn.Action
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.util.x
import com.neutrino.game.util.y
import com.neutrino.game.utility.VectorOperations
import squidpony.squidmath.Coord
import kotlin.math.pow
import kotlin.random.Random

open class EnemyAi(viewDistance: Int = 10): Ai(viewDistance) {

    /**
     * Constant values related to enemy detection and sensing
     */
    companion object {
        /** How long will the character chase the enemy */
        private val MAX_ENERGY = 15
        /** Radius of the detection square */
        private val DETECTION_RADIUS = 15
        /** Maximum distance that enemy can be sensed at */
        private val MAX_DETECTION_DISTANCE = VectorOperations.getLength(DETECTION_RADIUS.toFloat(), DETECTION_RADIUS.toFloat())
    }

    var sensedEnemyArray: MutableSet<Entity> = mutableSetOf()
        private set

    protected var targettedEnemy: Entity? = null

    var gotAttackedBy: Entity? = null
        set(value) {
            if (value != null) {
                energy += 10
                val displayDetection = !sensedEnemyArray.contains(value)
                searchTarget()
                if (targettedEnemy != null) {
                    currentBehavior = AiBehavior.TARGET_ENEMY
                    // TODO ECS Actors
//                    if (displayDetection)
//                        ActorVisuals.showAiIntention(character, AiIntentionIcons.ENEMY_DETECTED())
                }
            }
            field = value
        }

    /**
     * Position to which the character will return after exhausting energy
     */
    private var designatedPosition = Coord.get(0, 0)

    var currentBehavior: AiBehavior = AiBehavior.SENSE_ENEMIES

    protected var energy: Int = MAX_ENERGY
        set(value) {
            field =
                if (value > MAX_ENERGY)
                    MAX_ENERGY
                else if (value < 0)
                    0
                else
                    value
        }

    /**
     * Forces character to wait after losing aggro despite having enough energy to return
     */
    private var energyRecharged = 0

    override fun decide() {
        checkEnemyVisibility()

        when (currentBehavior) {
            AiBehavior.SENSE_ENEMIES -> {
                searchTarget()
                if (targettedEnemy != null) {
                    currentBehavior = AiBehavior.TARGET_ENEMY
                    // TODO ECS Actors
//                    ActorVisuals.showAiIntention(character, AiIntentionIcons.ENEMY_DETECTED())
                    return decide()
                }

                action = Action.WAIT
                energy++
            }
            AiBehavior.TARGET_ENEMY -> {
                designatedPosition = Coord.get(entity.get(Position::class)!!.x, entity.get(Position::class)!!.y)
                target(
                    targettedEnemy!!.get(Position::class)!!.x, targettedEnemy!!.get(Position::class)!!.y)

                if (action is Action.ATTACK)
                    energy += 5
                else
                    energy--

                if (energy == 0)
                    currentBehavior = AiBehavior.LOSE_AGGRO
            }
            AiBehavior.LOSE_AGGRO -> {
                if (energyRecharged >= 5) {
                    energyRecharged = 0
                    // If the enemy is still in view, can decide to attack it
                    if (targettedEnemy == null)
                        searchTarget()
                    if (targettedEnemy != null && Random.nextFloat() <= 0.5) {
                        currentBehavior = AiBehavior.TARGET_ENEMY
                        // TODO ECS Actors
//                        ActorVisuals.showAiIntention(character, AiIntentionIcons.ENEMY_DETECTED())
                        return decide()
                    }

                    setMoveList(designatedPosition.x, designatedPosition.y)
                    val returnPath = moveList.toList()
                    if (returnPath.isNotEmpty())
                        designatedPosition = returnPath[Random.nextInt(returnPath.size / 2, returnPath.size)]
                    else
                        println("Path is empty!")
                    currentBehavior = AiBehavior.RETURN
                    return decide()
                }

                gotAttackedBy = null
                action = Action.WAIT
                energy++
                energyRecharged++
                // TODO ECS Actors
//                ActorVisuals.showAiIntention(character, AiIntentionIcons.WAITING())
            }
            AiBehavior.RETURN -> {
                if (entity.get(Position::class)!!.x == designatedPosition!!.x && entity.get(Position::class)!!.y == designatedPosition!!.y) {
                    energy += MAX_ENERGY
                    currentBehavior = AiBehavior.SENSE_ENEMIES
                    return decide()
                }

                if (targettedEnemy == null)
                    searchTarget()
                // If the enemy is still sensed, add a probability to attack it
                if (targettedEnemy != null && Random.nextFloat() <= 0.137) {
                    // TODO ECS Actors
//                    ActorVisuals.showAiIntention(character, AiIntentionIcons.ENEMY_DETECTED())
                    currentBehavior = AiBehavior.TARGET_ENEMY
                    return decide()
                }

                moveTo(designatedPosition!!.x, designatedPosition!!.y)

                energy++
            }
            else -> {
                currentBehavior = AiBehavior.SENSE_ENEMIES
                return decide()
            }
        }
    }


    /**
     * Searches possible targets in a square area around the character
     * Sets the found character as the target
     */
    protected fun searchTarget() {
        /**
         * Tries to detect the enemy.
         * Takes into account distance, enemy stealth and character stealth as detection buff
         *
         * @return true if the enemy was detected
         */
        fun tryDetect(enemy: Entity): Boolean {
            val distance = VectorOperations.getDistance(entity.x, entity.y, enemy.x, enemy.y)
            var detectionProbability = 0.2f * 0.05f.pow(distance / MAX_DETECTION_DISTANCE - 0.04f)

            if (gotAttackedBy == enemy)
                detectionProbability += 0.15f

            // If the enemy is visible, increase detection probability
            if (fov[enemy.y][enemy.x]) {
                detectionProbability += 0.7f

                if (gotAttackedBy == enemy)
                    detectionProbability += 0.35f
            }

            return Random.nextFloat() <= detectionProbability + entity.get(DefensiveStats::class)!!.stealth - enemy.get(DefensiveStats::class)!!.stealth
        }

        val characterMap = entity.get(Position::class)!!.chunk.characterMap
        val xPos = entity.x
        val yPos = entity.y

        val left: Int = if (xPos - DETECTION_RADIUS <= 0) 0 else xPos - DETECTION_RADIUS
        val right: Int = if (xPos + DETECTION_RADIUS >= characterMap[0].size) characterMap[0].size - 1 else xPos + DETECTION_RADIUS
        val up: Int = if (yPos - DETECTION_RADIUS <= 0) 0 else yPos - DETECTION_RADIUS
        val down: Int = if (yPos + DETECTION_RADIUS >= characterMap.size) characterMap.size - 1 else yPos + DETECTION_RADIUS

        // Add every enemy in radius
        for (y in up .. down) {
            for (x in left .. right) {
                if (characterMap[y][x] != null &&
                    entity.get(Faction::class)!!.faction.enemies
                        .contains(characterMap[y][x]!!.get(Faction::class)!!.faction))
                    sensedEnemyArray.add(characterMap[y][x]!!)
            }
        }

        // Try to detect each enemy
        val iterator = sensedEnemyArray.iterator()
        while (iterator.hasNext()) {
            if (!tryDetect(iterator.next()))
                iterator.remove()
        }

        if (sensedEnemyArray.size == 0)
            return

        // Randomize to get single target
        targettedEnemy =
            if (sensedEnemyArray.size > 1)
                sensedEnemyArray.elementAt(Random.nextInt(sensedEnemyArray.size))
            else
                sensedEnemyArray.elementAt(0)
    }

    /**
     * If enemies are not visible, there is a chance of forgetting about them
     */
    private fun checkEnemyVisibility() {
        // Check the state of each enemy
        val iterator = sensedEnemyArray.iterator()
        while (iterator.hasNext()) {
            val enemy = iterator.next()
            if (!fov[enemy.get(Position::class)!!.y][enemy.get(Position::class)!!.x] && Random.nextFloat() <= 0.137) {
                iterator.remove()
                if (targettedEnemy == enemy) {
                    targettedEnemy = null
                    currentBehavior = AiBehavior.LOSE_AGGRO
                }
            }
        }
    }


    enum class AiBehavior {
        TARGET_ENEMY,
        LOSE_AGGRO,
        RETURN,
        SENSE_ENEMIES,
        GOTO_CHARACTER
    }

}