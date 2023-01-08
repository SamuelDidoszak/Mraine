package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.turn.Action
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.utility.VectorOperations
import squidpony.squidmath.Coord
import kotlin.math.pow
import kotlin.random.Random

class EnemyAi(val character: Character): Ai(character) {

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

    private var possibleTargetArray: ArrayList<Character> = ArrayList()

    private var targettedEnemy: Character? = null

    var gotAttackedBy: Character? = null
        set(value) {
            energy += 10
            field = value
        }

    /**
     * Position to which the character will return after exhausting energy
     */
    private var designatedPosition = Coord.get(character.xPos, character.yPos)

    var currentBehavior: AiBehavior = AiBehavior.SENSE_ENEMIES

    private var energy: Int = MAX_ENERGY
        set(value) {
            field =
                if (value > MAX_ENERGY)
                    MAX_ENERGY
                else if (value < 0)
                    0
                else
                    value
        }

    override fun decide() {
        when (currentBehavior) {
            AiBehavior.SENSE_ENEMIES -> {
                searchTarget(Turn.characterMap)
                if (targettedEnemy != null){
                    currentBehavior = AiBehavior.TARGET_ENEMY
                    return decide()
                }

                character.ai.action = Action.WAIT
                energy++
            }
            AiBehavior.TARGET_ENEMY -> {
                character.ai.target(
                    targettedEnemy!!.xPos, targettedEnemy!!.yPos,
                    Turn.dijkstraMap, Turn.mapImpassableList.plus(Turn.charactersUseCases.getImpassable()))

                if (character.ai.action is Action.ATTACK)
                    energy += 5
                else
                    energy--
            }
            AiBehavior.LOSE_AGGRO -> {
                if (energy >= 5) {
                    setMoveList(designatedPosition.x, designatedPosition.y, Turn.dijkstraMap, Turn.charactersUseCases.getImpassable())
                    val returnPath = moveList.toList()
                    designatedPosition = returnPath[Random.nextInt(returnPath.size / 2, returnPath.size)]
                    currentBehavior = AiBehavior.RETURN
                    return decide()
                }

                targettedEnemy = null
                gotAttackedBy = null
                character.ai.action = Action.WAIT
                energy++
            }
            AiBehavior.RETURN -> {
                if (character.xPos == designatedPosition!!.x && character.yPos == designatedPosition!!.y) {
                    currentBehavior = AiBehavior.SENSE_ENEMIES
                    return decide()
                }

                moveTo(designatedPosition!!.x, designatedPosition!!.y,
                    Turn.dijkstraMap, Turn.mapImpassableList.plus(Turn.charactersUseCases.getImpassable()))

                energy++
            }
        }

        if (energy == 0)
            currentBehavior = AiBehavior.LOSE_AGGRO
    }


    /**
     * Searches possible targets in a square area around the character
     * Sets the found character as the target
     */
    private fun searchTarget(characterMap: List<MutableList<Character?>>) {
        /**
         * Tries to detect the enemy.
         * Takes into account distance, enemy stealth and character stealth as detection buff
         *
         * @return true if the enemy was detected
         */
        fun tryDetect(enemy: Character): Boolean {
            val distance = VectorOperations.getDistance(character.xPos, character.yPos, enemy.xPos, enemy.yPos)
            var detectionProbability = 0.2f * 0.05f.pow(distance / MAX_DETECTION_DISTANCE - 0.04f)

            if (gotAttackedBy == enemy)
                detectionProbability += 0.15f

            // If the enemy is visible, increase detection probability
            if (fov[enemy.yPos][enemy.xPos]) {
                detectionProbability += 0.7f

                if (gotAttackedBy == enemy)
                    detectionProbability += 0.35f
            }

            return Random.nextFloat() <= detectionProbability + character.stealth - enemy.stealth
        }


        val left: Int = if (character.xPos - DETECTION_RADIUS <= 0) 0 else character.xPos - DETECTION_RADIUS
        val right: Int = if (character.xPos + DETECTION_RADIUS >= characterMap[0].size) characterMap[0].size - 1 else character.xPos + DETECTION_RADIUS
        val up: Int = if (character.yPos - DETECTION_RADIUS <= 0) 0 else character.yPos - DETECTION_RADIUS
        val down: Int = if (character.yPos + DETECTION_RADIUS >= characterMap.size) characterMap.size - 1 else character.yPos + DETECTION_RADIUS

        possibleTargetArray = ArrayList()

        // Add every enemy in radius
        for (x in left .. right) {
            for (y in up .. down) {
                if (characterMap[y][x] != null && character.characterAlignment.enemies.contains(characterMap[y][x]!!.characterAlignment))
                    possibleTargetArray.add(characterMap[y][x]!!)
            }
        }

        // Try to detect each enemy
        val iterator = possibleTargetArray.iterator()
        while (iterator.hasNext()) {
            if (!tryDetect(iterator.next()))
                iterator.remove()
        }

        if (possibleTargetArray.size == 0)
            return

        // Randomize to get single target
        targettedEnemy =
            if (possibleTargetArray.size > 1)
                possibleTargetArray[Random.nextInt(possibleTargetArray.size)]
            else
                possibleTargetArray[0]
    }


    enum class AiBehavior {
        TARGET_ENEMY,
        LOSE_AGGRO,
        RETURN,
        SENSE_ENEMIES
    }
}