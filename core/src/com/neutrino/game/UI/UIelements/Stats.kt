package com.neutrino.game.UI.UIelements

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.Level
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.Fonts
import com.neutrino.game.util.compareDelta
import com.neutrino.game.util.roundOneDecimal
import com.neutrino.game.util.setTextSameWidth
import ktx.scene2d.scene2d
import ktx.scene2d.table
import kotlin.math.roundToInt

class Stats: Table() {

    private val defensiveStats = Player.get(DefensiveStats::class)!!
    private val offensiveStats = Player.get(OffensiveStats::class)!!
    private enum class DamageEnums {
        Damage,
        Fire,
        Water,
        Air,
        Poison
    }

    private var border: Image? = null

    fun initialize(border: Image) {
        this.border = border
        clear()
        addStatsTable(border)
        refreshDamageLabelText()
        name = "stats"
    }

    fun refreshStats() {
        if (border == null) return
        initialize(border!!)
    }

    private fun formatDamageValues(min: Float, max: Float, addSpaces: Boolean = false): String {
        if (min.toInt() >= 10 || max.toInt() >= 10)
            return "${min.toInt()}" + (if (addSpaces) " - " else "-") + "${max.toInt()}"
        else if (((min - min.toInt()) * 10).toInt() > 0 || ((max - max.toInt()) * 10).toInt() > 0)
            return "${min.roundOneDecimal()}" + (if (addSpaces) " - " else "-") + "${max.roundOneDecimal()}"
        else
            return "${min.toInt()}" + (if (addSpaces) " - " else "-") + "${max.toInt()}"
    }

    private fun formatDamageText(type: DamageEnums): String {
        return when (type) {
            DamageEnums.Damage -> formatDamageValues(offensiveStats.damageMin, offensiveStats.damageMax)
            DamageEnums.Fire -> formatDamageValues(offensiveStats.fireDamageMin, offensiveStats.fireDamageMax)
            DamageEnums.Water -> formatDamageValues(offensiveStats.waterDamageMin, offensiveStats.waterDamageMax)
            DamageEnums.Air -> formatDamageValues(offensiveStats.airDamageMin, offensiveStats.airDamageMax)
            DamageEnums.Poison -> formatDamageValues(offensiveStats.poisonDamageMin, offensiveStats.poisonDamageMax)
        }
    }

    private fun addStatsTable(border: Image) {
        val width = border.width * 0.55f
        val title = scene2d.table {
            val name = TextraLabel(Player.name, Fonts.EQUIPMENT, Color.BLACK)
            name.align = Align.left
            add(name).padLeft(24f)

            val lvl = TextraLabel("lvl ${Player.get(Level::class)!!.level}", Fonts.EQUIPMENT, Color.BLACK)
            lvl.name = "level"
            lvl.align = Align.center
            add(lvl).fillX().expandX().center()

            val expValues = scene2d.table {
                val expValue = TextraLabel("${Player.get(Level::class)!!.experience}", Fonts.MATCHUP, Color.BLACK)
                expValue.name = "exp"
                expValue.align = Align.center
                add(expValue).fillX().expandX().uniform()
                add(TextraLabel("/", Fonts.MATCHUP, Color.BLACK))
                val expMax = TextraLabel("${Player.get(Level::class)!!.expRequiredForNextLevel()}", Fonts.MATCHUP, Color.BLACK)
                expMax.name = "expMax"
                expMax.align = Align.center
                add(expMax).fillX().expandX().uniform()
            }
            expValues.pack()
            add(expValues).top().right()
        }
        add(title).fillX().center().padBottom(12f)
        row()

        val stats1 = scene2d.table {
            val hpLabel = TextraLabel("Hp", Fonts.EQUIPMENT, ColorUtils.getStatColor("Hp"))
            add(hpLabel).width(width / 3).fillX().colspan(2).uniform()
            hpLabel.align = Align.left
            val hpValues = scene2d.table {
                val hpValue = TextraLabel("${defensiveStats.hp.roundOneDecimal()}", Fonts.EQUIPMENT, ColorUtils.getStatColor("Hp"))
                hpValue.name = "hp"
                hpValue.align = Align.center
                add(hpValue).fillX().expandX().uniform()
                add(TextraLabel("/", Fonts.EQUIPMENT, ColorUtils.getStatColor("Hp")))
                val hpMax = TextraLabel("${defensiveStats.hpMax.roundOneDecimal()}", Fonts.EQUIPMENT, ColorUtils.getStatColor("Hp"))
                hpMax.name = "hpMax"
                hpMax.align = Align.center
                add(hpMax).fillX().expandX().uniform()
            }
            add(hpValues).width(width / 3).colspan(2).uniform().fillX()
            row()

            val mpLabel = TextraLabel("Mp", Fonts.EQUIPMENT, ColorUtils.getStatColor("Mp"))
            mpLabel.align = Align.left
            add(mpLabel).fillX().width(width / 3).colspan(2).uniform()
            val mpValues = scene2d.table {
                val mpValue = TextraLabel("${defensiveStats.mp.roundOneDecimal()}", Fonts.EQUIPMENT, ColorUtils.getStatColor("Mp"))
                mpValue.name = "mp"
                mpValue.align = Align.center
                add(mpValue).fillX().expandX().uniform()
                add(TextraLabel("/", Fonts.EQUIPMENT, ColorUtils.getStatColor("Mp")))
                val mpMax = TextraLabel("${defensiveStats.mpMax.roundOneDecimal()}", Fonts.EQUIPMENT, ColorUtils.getStatColor("Mp"))
                mpMax.name = "mpMax"
                mpMax.align = Align.center
                add(mpMax).fillX().expandX().uniform()
            }
            add(mpValues).colspan(2).uniform().fillX()
            row()

            val strengthLabel = TextraLabel("Strength", Fonts.EQUIPMENT, ColorUtils.getStatColor("Strength"))
            strengthLabel.alignment = Align.left
            add(strengthLabel).fillX().width(width / 3).colspan(2).uniform()
            strengthLabel.align = Align.left
            val strength = TextraLabel("${offensiveStats.strength.roundOneDecimal()}", Fonts.EQUIPMENT, ColorUtils.getStatColor("Strength"))
            strength.name = "strength"
            strength.align = Align.center
            add(strength).fillX().colspan(2).uniform()
            row()

            val dexterityLabel = TextraLabel("Dexterity", Fonts.EQUIPMENT, ColorUtils.getStatColor("Dexterity"))
            dexterityLabel.align = Align.left
            add(dexterityLabel).fillX().width(width / 3).colspan(2).uniform()
            val dexterity = TextraLabel("${offensiveStats.dexterity.roundOneDecimal()}", Fonts.EQUIPMENT, ColorUtils.getStatColor("Dexterity"))
            dexterity.name = "dexterity"
            dexterity.align = Align.center
            add(dexterity).fillX().colspan(2).uniform()
            row()

            val intelligenceLabel = TextraLabel("Intelligence", Fonts.EQUIPMENT, ColorUtils.getStatColor("Intelligence"))
            intelligenceLabel.align = Align.left
            add(intelligenceLabel).fillX().width(width / 3).colspan(2).uniform()
            val intelligence = TextraLabel("${offensiveStats.intelligence.roundOneDecimal()}", Fonts.EQUIPMENT, ColorUtils.getStatColor("Intelligence"))
            intelligence.name = "intelligence"
            intelligence.align = Align.center
            add(intelligence).fillX().colspan(2).uniform()
            row()

            val luckLabel = TextraLabel("Luck", Fonts.EQUIPMENT, ColorUtils.getStatColor("Luck"))
            luckLabel.align = Align.left
            add(luckLabel).fillX().width(width / 3).colspan(2).uniform()
            val luck = TextraLabel("${offensiveStats.luck.roundOneDecimal()}", Fonts.EQUIPMENT, ColorUtils.getStatColor("Luck"))
            luck.name = "luck"
            luck.align = Align.center
            add(luck).fillX().colspan(2).uniform()
            row()

            val damageLabel = TextraLabel("Damage", Fonts.EQUIPMENT, ColorUtils.getStatColor("Damage"))
            damageLabel.align = Align.left
            damageLabel.name = "damageLabel"
            add(damageLabel).fillX().width(width / 3).colspan(2).uniform()
            val damageValues = scene2d.table {
                val damageValue = TextraLabel("${offensiveStats.damageMin.roundOneDecimal()}", Fonts.EQUIPMENT, ColorUtils.getStatColor("Damage"))
                damageValue.name = "damage"
                damageValue.align = Align.center
                add(damageValue).fillX().expandX().uniform()
                val damageSlash = TextraLabel("-", Fonts.EQUIPMENT, ColorUtils.getStatColor("Damage"))
                damageSlash.name = "damageSlash"
                add(damageSlash)
                val damageMax = TextraLabel("${offensiveStats.damageMax.roundOneDecimal()}", Fonts.EQUIPMENT, ColorUtils.getStatColor("Damage"))
                damageMax.name = "damageMax"
                damageMax.align = Align.center
                add(damageMax).fillX().expandX().uniform()
            }
            add(damageValues).colspan(2).uniform().fillX()
            row()

            val defenceLabel = TextraLabel("Defence", Fonts.EQUIPMENT, ColorUtils.getStatColor("Defence"))
            defenceLabel.align = Align.left
            add(defenceLabel).fillX().width(width / 3).colspan(2).uniform()
            val defence = TextraLabel("${defensiveStats.defence.roundOneDecimal()}", Fonts.EQUIPMENT, ColorUtils.getStatColor("Defence"))
            defence.name = "defence"
            defence.align = Align.center
            add(defence).fillX().colspan(2).uniform()
            row()
        }
        add(stats1).padBottom(12f)
        row()

        val stats2 = scene2d.table {
            val evasionLabel = TextraLabel("Evasion", Fonts.EQUIPMENT, Color.BLACK)
            evasionLabel.align = Align.left
            add(evasionLabel).fillX().width((width / 60) * 18).colspan(18).uniform()
            val evasion = TextraLabel("${(defensiveStats.evasion * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            evasion.name = "evasion"
            evasion.align = Align.center
            add(evasion).width((width / 60) * 12).colspan(12).uniform()

            val accuracyLabel = TextraLabel("Accuracy", Fonts.EQUIPMENT, Color.BLACK)
            accuracyLabel.align = Align.left
            add(accuracyLabel).fillX().width(width / 3).colspan(20).uniform()
            val accuracy = TextraLabel("${(offensiveStats.accuracy * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            accuracy.name = "accuracy"
            accuracy.align = Align.center
            add(accuracy).width(width / 6).colspan(10).uniform()
            row()

            val critChanceLabel = TextraLabel("Crit chance", Fonts.EQUIPMENT, Color.BLACK)
            critChanceLabel.align = Align.left
            add(critChanceLabel).align(Align.left).colspan(18).uniform()
            val critChance = TextraLabel("${(offensiveStats.criticalChance * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            critChance.name = "critChance"
            critChance.align = Align.center
            add(critChance).fillX().colspan(12).uniform()

            val critDamageLabel = TextraLabel("Crit damage", Fonts.EQUIPMENT, Color.BLACK)
            critDamageLabel.align = Align.left
            add(critDamageLabel).fillX().colspan(20).uniform()
            val critDamage = TextraLabel("${(offensiveStats.criticalDamage * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            critDamage.name = "critDamage"
            critDamage.align = Align.center
            add(critDamage).fillX().colspan(10).uniform()
            row()

            val movementSpeedLabel = TextraLabel("Movement", Fonts.EQUIPMENT, Color.BLACK)
            movementSpeedLabel.align = Align.left
            add(movementSpeedLabel).fillX().colspan(18).uniform()
            movementSpeedLabel.setBounds(0f, 0f, border.width / 3f, 100f)
            val movementSpeed = TextraLabel("${defensiveStats.movementSpeed.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            movementSpeed.name = "movementSpeed"
            movementSpeed.align = Align.center
            add(movementSpeed).fillX().colspan(12).uniform()

            val attackSpeedLabel = TextraLabel("Attack spd", Fonts.EQUIPMENT, Color.BLACK)
            attackSpeedLabel.align = Align.left
            add(attackSpeedLabel).fillX().colspan(20).uniform()
            val attackSpeed = TextraLabel("${offensiveStats.attackSpeed.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            attackSpeed.name = "attackSpeed"
            attackSpeed.align = Align.center
            add(attackSpeed).fillX().colspan(10).uniform()
            row().padTop(12f)

            val fireDamageLabel = TextraLabel("Fire dmg", Fonts.EQUIPMENT, ColorUtils.getStatColor("FireDamage"))
            fireDamageLabel.align = Align.left
            fireDamageLabel.name = "fireDamageLabel"
            add(fireDamageLabel).fillX().colspan(18)
            val fireDamage = TextraLabel(formatDamageText(DamageEnums.Fire), Fonts.EQUIPMENT, ColorUtils.getStatColor("FireDamage"))
            fireDamage.name = "fireDamage"
            fireDamage.align = Align.center
            add(fireDamage).fillX().colspan(12)

            val fireDefenceLabel = TextraLabel("Fire def", Fonts.EQUIPMENT, ColorUtils.getStatColor("FireDefence"))
            fireDefenceLabel.align = Align.left
            add(fireDefenceLabel).fillX().colspan(20)
            val fireDefence = TextraLabel("${(defensiveStats.fireDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, ColorUtils.getStatColor("FireDefence"))
            fireDefence.name = "fireDefence"
            fireDefence.align = Align.center
            add(fireDefence).fillX().colspan(10)
            row()

            val waterDamageLabel = TextraLabel("Water dmg", Fonts.EQUIPMENT, ColorUtils.getStatColor("WaterDamage"))
            waterDamageLabel.align = Align.left
            waterDamageLabel.name = "waterDamageLabel"
            add(waterDamageLabel).fillX().colspan(18)
            val waterDamage = TextraLabel(formatDamageText(DamageEnums.Water), Fonts.EQUIPMENT, ColorUtils.getStatColor("WaterDamage"))
            waterDamage.name = "waterDamage"
            waterDamage.align = Align.center
            add(waterDamage).fillX().colspan(12)

            val waterDefenceLabel = TextraLabel("Water def", Fonts.EQUIPMENT, ColorUtils.getStatColor("WaterDefence"))
            waterDefenceLabel.align = Align.left
            add(waterDefenceLabel).fillX().colspan(20)
            val waterDefence = TextraLabel("${(defensiveStats.waterDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, ColorUtils.getStatColor("WaterDefence"))
            waterDefence.name = "waterDefence"
            waterDefence.align = Align.center
            add(waterDefence).fillX().colspan(10)
            row()

            val airDamageLabel = TextraLabel("Air dmg", Fonts.EQUIPMENT, ColorUtils.getStatColor("AirDamage"))
            airDamageLabel.align = Align.left
            airDamageLabel.name = "airDamageLabel"
            add(airDamageLabel).fillX().colspan(18)
            val airDamage = TextraLabel(formatDamageText(DamageEnums.Air), Fonts.EQUIPMENT, ColorUtils.getStatColor("AirDamage"))
            airDamage.name = "airDamage"
            airDamage.align = Align.center
            add(airDamage).fillX().colspan(12)

            val airDefenceLabel = TextraLabel("Air def", Fonts.EQUIPMENT, ColorUtils.getStatColor("AirDefence"))
            airDefenceLabel.align = Align.left
            add(airDefenceLabel).fillX().colspan(20)
            val airDefence = TextraLabel("${(defensiveStats.airDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, ColorUtils.getStatColor("AirDefence"))
            airDefence.name = "airDefence"
            airDefence.align = Align.center
            add(airDefence).fillX().colspan(10)
            row()

            val poisonDamageLabel = TextraLabel("Poison dmg", Fonts.EQUIPMENT, ColorUtils.getStatColor("PoisonDamage"))
            poisonDamageLabel.align = Align.left
            poisonDamageLabel.name = "poisonDamageLabel"
            add(poisonDamageLabel).fillX().colspan(18)
            val poisonDamage = TextraLabel(formatDamageText(DamageEnums.Poison), Fonts.EQUIPMENT, ColorUtils.getStatColor("PoisonDamage"))
            poisonDamage.name = "poisonDamage"
            poisonDamage.align = Align.center
            add(poisonDamage).fillX().colspan(12)

            val poisonDefenceLabel = TextraLabel("Poison def", Fonts.EQUIPMENT, ColorUtils.getStatColor("PoisonDefence"))
            poisonDefenceLabel.align = Align.left
            add(poisonDefenceLabel).fillX().colspan(20)
            val poisonDefence = TextraLabel("${(defensiveStats.poisonDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, ColorUtils.getStatColor("PoisonDefence"))
            poisonDefence.name = "poisonDefence"
            poisonDefence.align = Align.center
            add(poisonDefence).fillX().colspan(10)
            row()
        }

        add(stats2)
        row()
        pack()
    }

    private fun getBiggestDamageType(): DamageEnums {
        val damageList = listOf(
            DamageEnums.Damage to (offensiveStats.damageMin + offensiveStats.damageMax) / 2f,
            DamageEnums.Fire to (offensiveStats.fireDamageMin + offensiveStats.fireDamageMax) / 2f,
            DamageEnums.Water to (offensiveStats.waterDamageMin + offensiveStats.waterDamageMax) / 2f,
            DamageEnums.Air to (offensiveStats.airDamageMin + offensiveStats.airDamageMax) / 2f,
            DamageEnums.Poison to (offensiveStats.poisonDamageMin + offensiveStats.poisonDamageMax) / 2f,
        )

        return damageList.maxWith { o1, o2 -> o1.second.compareDelta(o2.second) }.first
    }

    private fun refreshDamageLabelText() {
        fun TextraLabel.setStatColor(statName: String): TextraLabel {
            this.color = ColorUtils.getStatColor(statName)
            return this
        }

        when (getBiggestDamageType()) {
            DamageEnums.Damage -> { }
            DamageEnums.Fire -> {
                findActor<TextraLabel>("damageLabel").setStatColor("FireDamage").setTextSameWidth("Fire dmg")
                findActor<TextraLabel>("damage").setStatColor("FireDamage").setTextSameWidth("${offensiveStats.fireDamageMin.roundOneDecimal()}")
                findActor<TextraLabel>("damageMax").setStatColor("FireDamage").setTextSameWidth("${offensiveStats.fireDamageMax.roundOneDecimal()}")
                findActor<TextraLabel>("damageSlash").setStatColor("FireDamage")

                findActor<TextraLabel>("fireDamageLabel").setStatColor("Damage").setTextSameWidth("Damage")
                findActor<TextraLabel>("fireDamage").setStatColor("Damage").setTextSameWidth(formatDamageText(DamageEnums.Damage))
            }
            DamageEnums.Water-> {
                findActor<TextraLabel>("damageLabel").setStatColor("WaterDamage").setTextSameWidth("Water dmg")
                findActor<TextraLabel>("damage").setStatColor("WaterDamage").setTextSameWidth("${offensiveStats.waterDamageMin.roundOneDecimal()}")
                findActor<TextraLabel>("damageMax").setStatColor("WaterDamage").setTextSameWidth("${offensiveStats.waterDamageMax.roundOneDecimal()}")
                findActor<TextraLabel>("damageSlash").setStatColor("WaterDamage")

                findActor<TextraLabel>("waterDamageLabel").setStatColor("Damage").setTextSameWidth("Damage")
                findActor<TextraLabel>("waterDamage").setStatColor("Damage").setTextSameWidth(formatDamageText(DamageEnums.Damage))
            }
            DamageEnums.Air -> {
                findActor<TextraLabel>("damageLabel").setStatColor("AirDamage").setTextSameWidth("Air dmg")
                findActor<TextraLabel>("damage").setStatColor("AirDamage").setTextSameWidth("${offensiveStats.airDamageMin.roundOneDecimal()}")
                findActor<TextraLabel>("damageMax").setStatColor("AirDamage").setTextSameWidth("${offensiveStats.airDamageMax.roundOneDecimal()}")
                findActor<TextraLabel>("damageSlash").setStatColor("AirDamage")

                findActor<TextraLabel>("airDamageLabel").setStatColor("Damage").setTextSameWidth("Damage")
                findActor<TextraLabel>("airDamage").setStatColor("Damage").setTextSameWidth(formatDamageText(DamageEnums.Damage))
            }
            DamageEnums.Poison -> {
                findActor<TextraLabel>("damageLabel").setStatColor("PoisonDamage").setTextSameWidth("Poison dmg")
                findActor<TextraLabel>("damage").setStatColor("PoisonDamage").setTextSameWidth("${offensiveStats.poisonDamageMin.roundOneDecimal()}")
                findActor<TextraLabel>("damageMax").setStatColor("PoisonDamage").setTextSameWidth("${offensiveStats.poisonDamageMax.roundOneDecimal()}")
                findActor<TextraLabel>("damageSlash").setStatColor("PoisonDamage")

                findActor<TextraLabel>("poisonDamageLabel").setStatColor("Damage").setTextSameWidth("Damage")
                findActor<TextraLabel>("poisonDamage").setStatColor("Damage").setTextSameWidth(formatDamageText(DamageEnums.Damage))
            }
        }
    }
}