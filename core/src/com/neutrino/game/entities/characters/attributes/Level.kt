package com.neutrino.game.entities.characters.attributes

import com.badlogic.gdx.graphics.Color
import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.util.visuals.Visuals
import com.neutrino.game.graphics.utility.ColorUtils.toTextraColor

class Level: Attribute() {

    var level: Int = 1
        private set
    var experience: Int = 0
        private set

    var statsPoints: Int = 0
    var skillPoints: Int = 0

    fun addExp(exp: Int) {
        experience += exp
        if (experience >= expRequiredForNextLevel()) {
            experience -= expRequiredForNextLevel()
            level++
            statsPoints += 3
            skillPoints += skillPointsPerLevel[level - 1]

            (Player as Character).setAnimation("item", "idle")
            Player.get(DefensiveStats::class)!!.hp = Player.get(DefensiveStats::class)!!.hpMax
            Visuals.showText(Player, Color(80 / 255f, 208 / 255f, 121 / 255f, 1f).toTextraColor() +
                    "[BOLD]{HANG=0.75;0.5}{SLIDE}Level up!")
            GlobalData.notifyObservers(GlobalDataType.LEVELUP)
        }
    }

    fun expUntilNextLevel(): Int {
        return levelExp[level] - experience
    }

    fun expRequiredForNextLevel(): Int = levelExp[level]

    fun nextLevelPercent(): Float {
        return experience / levelExp[level].toFloat()
    }

    private companion object {
        val levelExp: List<Int> = listOf(
            0,
            50,
            125,
            325,
            700,
            1100,
            1800,
            3000,
            5000,
            8500
        )
        val skillPointsPerLevel: List<Int> = listOf(
            0,
            1,
            0,
            1,
            0,
            1,
            0,
            1,
            0,
            1,
            0,
            1
        )
    }
}