package com.neutrino.game.UI.UIelements

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.Fonts
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.roundOneDecimal
import com.neutrino.game.setTextSameWidth
import ktx.scene2d.scene2d
import ktx.scene2d.table
import kotlin.math.roundToInt

class Stats: Table() {

    fun initialize(border: Image) {
        addStatsTable(border)
        refreshDamageLabelText()
        name = "stats"
    }

    private fun addStatsTable(border: Image) {
        val width = border.width * 0.55f
        val title = scene2d.table {
            val name = TextraLabel(Player.name, Fonts.EQUIPMENT, Color.BLACK)
            name.align = Align.left
            add(name).padLeft(24f)

            val lvl = TextraLabel("lvl ${Player.level}", Fonts.EQUIPMENT, Color.BLACK)
            lvl.name = "level"
            lvl.align = Align.center
            add(lvl).fillX().expandX().center()

            val expValues = scene2d.table {
                val expValue = TextraLabel("${Player.experience}", Fonts.MATCHUP, Color.BLACK)
                expValue.name = "exp"
                expValue.align = Align.center
                add(expValue).fillX().expandX().uniform()
                add(TextraLabel("/", Fonts.MATCHUP, Color.BLACK))
                val expMax = TextraLabel("2137", Fonts.MATCHUP, Color.BLACK)
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
            val hpLabel = TextraLabel("Hp", Fonts.EQUIPMENT, Color.BLACK)
            add(hpLabel).width(width / 3).fillX().colspan(2).uniform()
            hpLabel.align = Align.left
            val hpValues = scene2d.table {
                val hpValue = TextraLabel("${Player.hp.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                hpValue.name = "hp"
                hpValue.align = Align.center
                add(hpValue).fillX().expandX().uniform()
                add(TextraLabel("/", Fonts.EQUIPMENT, Color.BLACK))
                val hpMax = TextraLabel("${Player.hpMax.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                hpMax.name = "hpMax"
                hpMax.align = Align.center
                add(hpMax).fillX().expandX().uniform()
            }
            add(hpValues).width(width / 3).colspan(2).uniform().fillX()
            row()

            val mpLabel = TextraLabel("Mp", Fonts.EQUIPMENT, Color.BLACK)
            mpLabel.align = Align.left
            add(mpLabel).fillX().width(width / 3).colspan(2).uniform()
            val mpValues = scene2d.table {
                val mpValue = TextraLabel("${Player.mp.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                mpValue.name = "mp"
                mpValue.align = Align.center
                add(mpValue).fillX().expandX().uniform()
                add(TextraLabel("/", Fonts.EQUIPMENT, Color.BLACK))
                val mpMax = TextraLabel("${Player.mpMax.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                mpMax.name = "mpMax"
                mpMax.align = Align.center
                add(mpMax).fillX().expandX().uniform()
            }
            add(mpValues).colspan(2).uniform().fillX()
            row()

            val strengthLabel = TextraLabel("Strength", Fonts.EQUIPMENT, Color.BLACK)
            strengthLabel.alignment = Align.left
            add(strengthLabel).fillX().width(width / 3).colspan(2).uniform()
            strengthLabel.align = Align.left
            val strength = TextraLabel("${Player.strength.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            strength.name = "strength"
            strength.align = Align.center
            add(strength).fillX().colspan(2).uniform()
            row()

            val dexterityLabel = TextraLabel("Dexterity", Fonts.EQUIPMENT, Color.BLACK)
            dexterityLabel.align = Align.left
            add(dexterityLabel).fillX().width(width / 3).colspan(2).uniform()
            val dexterity = TextraLabel("${Player.dexterity.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            dexterity.name = "dexterity"
            dexterity.align = Align.center
            add(dexterity).fillX().colspan(2).uniform()
            row()

            val intelligenceLabel = TextraLabel("Intelligence", Fonts.EQUIPMENT, Color.BLACK)
            intelligenceLabel.align = Align.left
            add(intelligenceLabel).fillX().width(width / 3).colspan(2).uniform()
            val intelligence = TextraLabel("${Player.intelligence.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            intelligence.name = "intelligence"
            intelligence.align = Align.center
            add(intelligence).fillX().colspan(2).uniform()
            row()

            val luckLabel = TextraLabel("Luck", Fonts.EQUIPMENT, Color.BLACK)
            luckLabel.align = Align.left
            add(luckLabel).fillX().width(width / 3).colspan(2).uniform()
            val luck = TextraLabel("${Player.luck.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            luck.name = "luck"
            luck.align = Align.center
            add(luck).fillX().colspan(2).uniform()
            row()

            val damageLabel = TextraLabel("Damage", Fonts.EQUIPMENT, Color.BLACK)
            damageLabel.align = Align.left
            damageLabel.name = "damageLabel"
            add(damageLabel).fillX().width(width / 3).colspan(2).uniform()
            val damageValues = scene2d.table {
                val damageValue = TextraLabel("${(Player.damage - Player.damageVariation).roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                damageValue.name = "damage"
                damageValue.align = Align.center
                add(damageValue).fillX().expandX().uniform()
                add(TextraLabel("-", Fonts.EQUIPMENT, Color.BLACK))
                val damageMax = TextraLabel("${(Player.damage + Player.damageVariation).roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                damageMax.name = "damageMax"
                damageMax.align = Align.center
                add(damageMax).fillX().expandX().uniform()
            }
            add(damageValues).colspan(2).uniform().fillX()
            row()

            val defenceLabel = TextraLabel("Defence", Fonts.EQUIPMENT, Color.BLACK)
            defenceLabel.align = Align.left
            add(defenceLabel).fillX().width(width / 3).colspan(2).uniform()
            val defence = TextraLabel("${Player.defence.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
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
            add(evasionLabel).fillX().width(width / 3).colspan(2).uniform()
            val evasion = TextraLabel("${(Player.evasion * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            evasion.name = "evasion"
            evasion.align = Align.center
            add(evasion).width(width / 6).colspan(1).uniform()

            val accuracyLabel = TextraLabel("Accuracy", Fonts.EQUIPMENT, Color.BLACK)
            accuracyLabel.align = Align.left
            add(accuracyLabel).fillX().width(width / 3).colspan(2).uniform()
            val accuracy = TextraLabel("${(Player.accuracy * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            accuracy.name = "accuracy"
            accuracy.align = Align.center
            add(accuracy).width(width / 6).colspan(1).uniform()
            row()

            val critChanceLabel = TextraLabel("Crit chance", Fonts.EQUIPMENT, Color.BLACK)
            critChanceLabel.align = Align.left
            add(critChanceLabel).fillX().colspan(2).uniform()
            val critChance = TextraLabel("${(Player.criticalChance * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            critChance.name = "critChance"
            critChance.align = Align.center
            add(critChance).fillX().colspan(1).uniform()

            val critDamageLabel = TextraLabel("Crit damage", Fonts.EQUIPMENT, Color.BLACK)
            critDamageLabel.align = Align.left
            add(critDamageLabel).fillX().colspan(2).uniform()
            val critDamage = TextraLabel("${(Player.criticalDamage * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            critDamage.name = "critDamage"
            critDamage.align = Align.center
            add(critDamage).fillX().colspan(1).uniform()
            row()

            val movementSpeedLabel = TextraLabel("Movement", Fonts.EQUIPMENT, Color.BLACK)
            movementSpeedLabel.align = Align.left
            add(movementSpeedLabel).fillX().colspan(2).uniform()
            movementSpeedLabel.setBounds(0f, 0f, border.width / 3f, 100f)
            val movementSpeed = TextraLabel("${Player.movementSpeed.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            movementSpeed.name = "movementSpeed"
            movementSpeed.align = Align.center
            add(movementSpeed).fillX().colspan(1).uniform()

            val attackSpeedLabel = TextraLabel("Attack spd", Fonts.EQUIPMENT, Color.BLACK)
            attackSpeedLabel.align = Align.left
            add(attackSpeedLabel).fillX().colspan(2).uniform()
            val attackSpeed = TextraLabel("${Player.attackSpeed.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            attackSpeed.name = "attackSpeed"
            attackSpeed.align = Align.center
            add(attackSpeed).fillX().colspan(1).uniform()
            row().padTop(12f)

            val fireDamageLabel = TextraLabel("Fire dmg", Fonts.EQUIPMENT, Color.BLACK)
            fireDamageLabel.align = Align.left
            fireDamageLabel.name = "fireDamageLabel"
            add(fireDamageLabel).fillX().colspan(2)
            val fireDamage = TextraLabel("${Player.fireDamage.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            fireDamage.name = "fireDamage"
            fireDamage.align = Align.center
            add(fireDamage).fillX().colspan(1)

            val fireDefenceLabel = TextraLabel("Fire def", Fonts.EQUIPMENT, Color.BLACK)
            fireDefenceLabel.align = Align.left
            add(fireDefenceLabel).fillX().colspan(2)
            val fireDefence = TextraLabel("${(Player.fireDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            fireDefence.name = "fireDefence"
            fireDefence.align = Align.center
            add(fireDefence).fillX().colspan(1)
            row()

            val waterDamageLabel = TextraLabel("Water dmg", Fonts.EQUIPMENT, Color.BLACK)
            waterDamageLabel.align = Align.left
            waterDamageLabel.name = "waterDamageLabel"
            add(waterDamageLabel).fillX().colspan(2)
            val waterDamage = TextraLabel("${Player.waterDamage.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            waterDamage.name = "waterDamage"
            waterDamage.align = Align.center
            add(waterDamage).fillX().colspan(1)

            val waterDefenceLabel = TextraLabel("Water def", Fonts.EQUIPMENT, Color.BLACK)
            waterDefenceLabel.align = Align.left
            add(waterDefenceLabel).fillX().colspan(2)
            val waterDefence = TextraLabel("${(Player.waterDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            waterDefence.name = "waterDefence"
            waterDefence.align = Align.center
            add(waterDefence).fillX().colspan(1)
            row()

            val earthDamageLabel = TextraLabel("Earth dmg", Fonts.EQUIPMENT, Color.BLACK)
            earthDamageLabel.align = Align.left
            earthDamageLabel.name = "earthDamageLabel"
            add(earthDamageLabel).fillX().colspan(2)
            val earthDamage = TextraLabel("${Player.earthDamage.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            earthDamage.name = "earthDamage"
            earthDamage.align = Align.center
            add(earthDamage).fillX().colspan(1)

            val earthDefenceLabel = TextraLabel("Earth def", Fonts.EQUIPMENT, Color.BLACK)
            earthDefenceLabel.align = Align.left
            add(earthDefenceLabel).fillX().colspan(2)
            val earthDefence = TextraLabel("${(Player.earthDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            earthDefence.name = "earthDefence"
            earthDefence.align = Align.center
            add(earthDefence).fillX().colspan(1)
            row()

            val airDamageLabel = TextraLabel("Air dmg", Fonts.EQUIPMENT, Color.BLACK)
            airDamageLabel.align = Align.left
            airDamageLabel.name = "airDamageLabel"
            add(airDamageLabel).fillX().colspan(2)
            val airDamage = TextraLabel("${Player.airDamage.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            airDamage.name = "airDamage"
            airDamage.align = Align.center
            add(airDamage).fillX().colspan(1)

            val airDefenceLabel = TextraLabel("Air def", Fonts.EQUIPMENT, Color.BLACK)
            airDefenceLabel.align = Align.left
            add(airDefenceLabel).fillX().colspan(2)
            val airDefence = TextraLabel("${(Player.airDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            airDefence.name = "airDefence"
            airDefence.align = Align.center
            add(airDefence).fillX().colspan(1)
            row()

            val poisonDamageLabel = TextraLabel("Poison dmg", Fonts.EQUIPMENT, Color.BLACK)
            poisonDamageLabel.align = Align.left
            poisonDamageLabel.name = "poisonDamageLabel"
            add(poisonDamageLabel).fillX().colspan(2)
            val poisonDamage = TextraLabel("${Player.poisonDamage.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            poisonDamage.name = "poisonDamage"
            poisonDamage.align = Align.center
            add(poisonDamage).fillX().colspan(1)

            val poisonDefenceLabel = TextraLabel("Poison def", Fonts.EQUIPMENT, Color.BLACK)
            poisonDefenceLabel.align = Align.left
            add(poisonDefenceLabel).fillX().colspan(2)
            val poisonDefence = TextraLabel("${(Player.poisonDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            poisonDefence.name = "poisonDefence"
            poisonDefence.align = Align.center
            add(poisonDefence).fillX().colspan(1)
            row()
        }

        add(stats2)
        row()
        pack()
    }

    fun refreshStats() {
        val uniqueStats: MutableSet<StatsEnum> = mutableSetOf()
        val otherData: MutableSet<String> = mutableSetOf()

        for (data in GlobalData.getData(GlobalDataType.PLAYERSTAT)) {
            when (data) {
                is StatsEnum -> uniqueStats.add(data)
                is String -> otherData.add(data)
            }
        }

        for (stat in uniqueStats)
            setStatLabelText(stat)

        for (other in otherData) {
            when (other) {
                "level" -> {
                    findActor<TextraLabel>("level").setText("lvl ${Player.level}")
                    findActor<TextraLabel>("expMax").setText("1237")
                }
            }
        }

        val expData = GlobalData.getData(GlobalDataType.PLAYEREXP)
        if (expData.isNotEmpty()) {
            findActor<TextraLabel>("exp").setText("${Player.experience.roundOneDecimal()}")
            expData.clear()
        }
        val hpData = GlobalData.getData(GlobalDataType.PLAYERHP)
        if (hpData.isNotEmpty()) {
            findActor<TextraLabel>("hp").setTextSameWidth("${Player.hp.roundOneDecimal()}")
            hpData.clear()
        }
        val mpData = GlobalData.getData(GlobalDataType.PLAYERMANA)
        if (mpData.isNotEmpty()) {
            findActor<TextraLabel>("mp").setTextSameWidth("${Player.mp.roundOneDecimal()}")
            mpData.clear()
        }

        GlobalData.getData(GlobalDataType.PLAYERSTAT).clear()
    }

    private fun refreshDamageLabelText() {
        val biggestDamageType = maxOf(Player.damage, Player.fireDamage, Player.waterDamage, Player.earthDamage, Player.airDamage, Player.poisonDamage)
        val damageLabel = findActor<TextraLabel>("damageLabel")
        when (biggestDamageType.roundToInt()) {
            Player.damage.roundToInt() -> {
                if (damageLabel.storedText != "Damage") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Damage")
                }
                findActor<TextraLabel>("damage").setTextSameWidth("${(Player.damage - Player.damageVariation).roundOneDecimal()}")
                findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.damage + Player.damageVariation).roundOneDecimal()}")
            }
            Player.fireDamage.roundToInt() -> {
                if (damageLabel.storedText != "Fire dmg") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Fire dmg")
                    findActor<TextraLabel>("fireDamageLabel").setTextSameWidth("Damage")
                    findActor<TextraLabel>("fireDamage").setTextSameWidth("${Player.damage}")
                }
                findActor<TextraLabel>("damage").setTextSameWidth("${(Player.fireDamage - Player.damageVariation).roundOneDecimal()}")
                findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.fireDamage + Player.damageVariation).roundOneDecimal()}")
            }
            Player.waterDamage.roundToInt() -> {
                if (damageLabel.storedText != "Water dmg") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Water dmg")
                    findActor<TextraLabel>("poisonDamageLabel").setTextSameWidth("Damage")
                    findActor<TextraLabel>("poisonDamage").setTextSameWidth("${Player.damage}")
                }
                findActor<TextraLabel>("damage").setTextSameWidth("${(Player.waterDamage - Player.damageVariation).roundOneDecimal()}")
                findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.waterDamage + Player.damageVariation).roundOneDecimal()}")
            }
            Player.earthDamage.roundToInt() -> {
                if (damageLabel.storedText != "Earth dmg") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Earth dmg")
                    findActor<TextraLabel>("poisonDamageLabel").setTextSameWidth("Damage")
                    findActor<TextraLabel>("poisonDamage").setTextSameWidth("${Player.damage}")
                }
                findActor<TextraLabel>("damage").setTextSameWidth("${(Player.earthDamage - Player.damageVariation).roundOneDecimal()}")
                findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.earthDamage + Player.damageVariation).roundOneDecimal()}")
            }
            Player.airDamage.roundToInt() -> {
                if (damageLabel.storedText != "Air dmg") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Air dmg")
                    findActor<TextraLabel>("poisonDamageLabel").setTextSameWidth("Damage")
                    findActor<TextraLabel>("poisonDamage").setTextSameWidth("${Player.damage}")
                }
                findActor<TextraLabel>("damage").setTextSameWidth("${(Player.airDamage - Player.damageVariation).roundOneDecimal()}")
                findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.airDamage + Player.damageVariation).roundOneDecimal()}")
            }
            Player.poisonDamage.roundToInt() -> {
                if (damageLabel.storedText != "Poison dmg") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Poison dmg")
                    findActor<TextraLabel>("poisonDamageLabel").setTextSameWidth("Damage")
                    findActor<TextraLabel>("poisonDamage").setTextSameWidth("${Player.damage}")
                }
                findActor<TextraLabel>("damage").setTextSameWidth("${(Player.poisonDamage - Player.damageVariation).roundOneDecimal()}")
                findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.poisonDamage + Player.damageVariation).roundOneDecimal()}")
            }
        }
    }

    private fun setStatLabelText(stat: StatsEnum) {
        when (stat) {
            StatsEnum.HPMAX -> findActor<TextraLabel>("hpMax").setTextSameWidth("${Player.hpMax.roundOneDecimal()}")
            StatsEnum.MPMAX -> findActor<TextraLabel>("mpMax").setTextSameWidth("${Player.mpMax.roundOneDecimal()}")
            StatsEnum.STRENGTH -> findActor<TextraLabel>("strength").setTextSameWidth("${Player.strength.roundOneDecimal()}")
            StatsEnum.DEXTERITY -> findActor<TextraLabel>("dexterity").setTextSameWidth("${Player.dexterity.roundOneDecimal()}")
            StatsEnum.INTELLIGENCE -> findActor<TextraLabel>("intelligence").setTextSameWidth("${Player.intelligence.roundOneDecimal()}")
            StatsEnum.LUCK -> findActor<TextraLabel>("luck").setTextSameWidth("${Player.luck.roundOneDecimal()}")
            StatsEnum.DAMAGE, StatsEnum.DAMAGEVARIATION -> refreshDamageLabelText()
            StatsEnum.DEFENCE -> findActor<TextraLabel>("defence").setTextSameWidth("${Player.defence.roundOneDecimal()}")
            StatsEnum.EVASION -> findActor<TextraLabel>("evasion").setTextSameWidth("${(Player.evasion * 100).roundToInt()}%")
            StatsEnum.ACCURACY -> findActor<TextraLabel>("accuracy").setTextSameWidth("${(Player.accuracy * 100).roundToInt()}%")
            StatsEnum.CRITICALCHANCE -> findActor<TextraLabel>("critChance").setTextSameWidth("${(Player.criticalChance * 100).roundToInt()}%")
            StatsEnum.CRITICALDAMAGE -> findActor<TextraLabel>("critDamage").setTextSameWidth("${(Player.criticalDamage * 100).roundToInt()}%")
            StatsEnum.MOVEMENTSPEED -> findActor<TextraLabel>("movementSpeed").setTextSameWidth("${Player.movementSpeed.roundOneDecimal()}")
            StatsEnum.ATTACKSPEED -> findActor<TextraLabel>("attackSpeed").setTextSameWidth("${Player.attackSpeed.roundOneDecimal()}")

            StatsEnum.FIREDAMAGE -> refreshDamageLabelText()
            StatsEnum.WATERDAMAGE -> refreshDamageLabelText()
            StatsEnum.EARTHDAMAGE -> refreshDamageLabelText()
            StatsEnum.AIRDAMAGE -> refreshDamageLabelText()
            StatsEnum.POISONDAMAGE -> refreshDamageLabelText()

            StatsEnum.FIREDEFENCE -> findActor<TextraLabel>("fireDefence").setTextSameWidth("${(Player.fireDefence * 100).roundToInt()}%")
            StatsEnum.WATERDEFENCE -> findActor<TextraLabel>("waterDefence").setTextSameWidth("${(Player.waterDefence * 100).roundToInt()}%")
            StatsEnum.EARTHDEFENCE -> findActor<TextraLabel>("earthDefence").setTextSameWidth("${(Player.earthDefence * 100).roundToInt()}%")
            StatsEnum.AIRDEFENCE -> findActor<TextraLabel>("airDefence").setTextSameWidth("${(Player.airDefence * 100).roundToInt()}%")
            StatsEnum.POISONDEFENCE -> findActor<TextraLabel>("poisonDefence").setTextSameWidth("${(Player.poisonDefence * 100).roundToInt()}%")

            StatsEnum.RANGE -> {}
            StatsEnum.RANGETYPE -> {}
            StatsEnum.STEALTH -> {}
        }
    }
}