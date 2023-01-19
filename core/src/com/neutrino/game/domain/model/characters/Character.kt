package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Pools
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.EventDispatcher
import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.Constants
import com.neutrino.game.Constants.MoveSpeed
import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.characters.utility.*
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.systems.CharacterTag
import com.neutrino.game.domain.model.systems.attack.Attack
import com.neutrino.game.domain.model.systems.attack.BasicAttack
import com.neutrino.game.domain.model.systems.attack.utility.AttackData
import com.neutrino.game.domain.model.systems.attack.utility.Attackable
import com.neutrino.game.domain.model.systems.event.types.EventHeal
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.domain.model.utility.ColorUtils
import com.neutrino.game.domain.use_case.Shaderable
import com.neutrino.game.graphics.shaders.OutlineShader
import com.neutrino.game.graphics.shaders.ShaderParametered
import squidpony.squidmath.Coord
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.createInstance

abstract class Character(
    var xPos: Int,
    var yPos: Int,
    var turn: Double
): Group(), TextureHaver, Animated, Shaderable, Stats, Randomization, Attackable {
    override var strength: Float = 0f
        set(value) {
            val difference = value - Player.strength
            field = value
            hpMax += difference * 5f
            hp += difference * 5f
            damage += difference * 0.5f}
    override var dexterity: Float = 0f
        set(value) {
            val difference = value - Player.dexterity
            field = value
            evasion += difference * 0.015f
            accuracy += difference * 0.02f
            stealth += difference * 0.015f}
    override var intelligence: Float = 0f
        set(value) {
            val difference = value - Player.intelligence
            field = value
            mpMax += difference * 5f
            mp += difference * 5f}
    override var luck: Float = 0f
        set(value) {
            val difference = value - Player.luck
            field = value
            criticalChance += difference * 0.015f}

    // Stat initialization for character boilerplate reduction
    override var hp: Float = 0f
    override var mp: Float = 0f
    override var evasion: Float = 0f
    override var accuracy: Float = 1f
    override var attackSpeed: Double = 1.0
    override var movementSpeed: Double = 1.0
    override var criticalDamage: Float = 2f
    override var range: Int = 1
    override var rangeType: RangeType = RangeType.SQUARE
    override var stealth: Float = 0f
    // environmental stats
    override var fireDamage: Float = 0f
    override var waterDamage: Float = 0f
    override var earthDamage: Float = 0f
    override var airDamage: Float = 0f
    override var poisonDamage: Float = 0f
    override var fireDefence: Float = 0f
    override var waterDefence: Float = 0f
    override var earthDefence: Float = 0f
    override var airDefence: Float = 0f
    override var poisonDefence: Float = 0f

    override var mirrored: Boolean = false
    override var shaders: ArrayList<ShaderParametered?> = ArrayList(1)

    /**
     * Basic attack when no item is equipped
     */
    open var basicAttack: Attack = BasicAttack(mapOf(StatsEnum.DAMAGE to 0f))
    /**
     * Primary attack
     */
    open var primaryAttack: Attack = basicAttack

    /**
     * Maximum viewing distance
     */
    open var viewDistance: Int = 10

    open var characterAlignment: CharacterAlignment = CharacterAlignment.ENEMY

    /** List of item drops */
    open val possibleItemDropList: List<Pair<KClass<Item>, Double>> = listOf()
    private val itemtemDropList: MutableList<Item> = ArrayList()
    init {
        val infoGroup = Group()
        this.addActor(infoGroup)
        infoGroup.name = "infoGroup"
        infoGroup.setPosition(0f, 32f * 4)

        val nameLabel = TextraLabel("", KnownFonts.getStandardFamily())
        nameLabel.name = "name"
        infoGroup.addActor(nameLabel)
    }

    override val randomizationProbability: Float = 0.3f
    override var randomizationMultiplier: Float = 0f

    /**
     * This method is called only once, after initialization of Character implementation
     * Passing values here makes sure that they are initialized
     */
    fun initialize(name: String?) {
        hp = hpMax
        mp = mpMax
        super.setName(name)
        this.findActor<TextraLabel>("name").setText("[@Cozette][WHITE][%175]$name")
        (this.getChild(0) as Group).addActor(HpBar(hp, hpMax))

        val turnBar = TurnBar(turn, movementSpeed)
        this.findActor<Group>("infoGroup").addActor(turnBar)

        // TODO if unkillable characters will be added, add this::class check

        possibleItemDropList.forEach {
            if (Constants.RandomGenerator.nextDouble() < it.second)
                itemtemDropList.add(it.first.createInstance())
        }
    }

    abstract val description: String

    abstract val textureSrc: String
    override val textureHaver: TextureHaver = this

    open val ai: Ai = Ai(this)
    val eventArray: CharacterEventArray = CharacterEventArray()

    val tags: HashMap<KClass<out CharacterTag>, CharacterTag> = HashMap()

    fun addTag(tag: CharacterTag) {
        tags.put(tag::class, tag)
    }

    fun <K: CharacterTag> getTag(tag: KClass<K>): K? {
        if (tags[tag] == null)
            return null

        return tag.cast(tags[tag])
    }

    override var textureList:  List<TextureAtlas.AtlasRegion> = listOf()
    override fun loadTextures(atlas: TextureAtlas) {
        textureList = buildList {
            for (name in textureNames) {
                add(atlas.findRegion(name))
            } }
    }

    override fun getTexture(name: String): TextureAtlas.AtlasRegion {
        return textureList.find { it.name.toString() == name }!!
    }

    override fun setTexture(name: String) {
        texture = getTexture(name)
        setBounds(xPos * 64f, parent.height - yPos * 64f, textureHaver.texture.regionWidth.toFloat() * 4, textureHaver.texture.regionHeight.toFloat() * 4)
        // Has to be positioned after the initial drawing, so the label knows its size
        val nameLabel = (this.getChild(0) as Group).getChild(0)
        nameLabel.setPosition(nameLabel.x + 32 - nameLabel.width / 2, nameLabel.y)
    }

    fun updateTurnBar(forceUpdateMovementColor: Boolean = false) {
        val turnBar =  this.findActor<TurnBar>("turnBar")
        turnBar?.update(this.turn, this.movementSpeed, forceUpdateMovementColor)
    }

    val outlineColor = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)


    override fun draw(batch: Batch?, parentAlpha: Float) {
        // required for fading
        batch?.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        batch?.draw(textureHaver.texture, if (!mirrored) x else x + width, y, originX, originY, if (!mirrored) width else width * -1, height, scaleX, scaleY, rotation)

        for (shader in shaders) {
            if (shader is OutlineShader)
                shader.setBoundaries(texture)
            shader?.applyToBatch(batch)
            batch?.draw(textureHaver.texture, if (!mirrored) x else x + width, y, originX, originY, if (!mirrored) width else width * -1, height, scaleX, scaleY, rotation)
        }
        if (shaders.isNotEmpty())
            batch?.shader = null

        super.draw(batch, parentAlpha)

        color.a = 1f
        batch?.color = color
    }

    open fun move(xPos: Int, yPos: Int, speed: Float = MoveSpeed) {
        this.addAction(Actions.moveTo(xPos * 64f, parent.height - yPos * 64f, speed))
        if (xPos != this.xPos)
            mirrored = xPos < this.xPos
        this.xPos = xPos
        this.yPos = yPos
    }

    fun getDamage(damage: Float, type: String): Float {
        var finalDamage: Float
        // for optimization, instead of creating Colors, pure rgb values can be passed
        var damageColor: Color = Color(0f, 0f, 0f, 1f)
        val color: Color
        when (type) {
            "physical" -> {
                finalDamage = damage * damage / (damage + defence)
                color = Color(255f, 0f, 0f, 1f)
            }
            "bleeding" -> {
                finalDamage = damage
                color = Color(255f, 0f, 0f, 1f)
            }
            "fire" -> {
                finalDamage = damage * (1 - fireDefence)
                color = Color(255f, 128f, 0f, 1f)
            }
            "water" -> {
                finalDamage = damage * (1 - waterDefence)
                color = Color(0f, 0f, 255f, 1f)
            }
            "earth" -> {
                finalDamage = damage * (1 - earthDefence)
                color = Color(0f, 255f, 0f, 1f)
            }
            "air" -> {
                finalDamage = damage * (1 - airDefence)
                color = Color(0f, 255f, 255f, 1f)
            }
            "poison" -> {
                finalDamage = damage * (1 - poisonDefence)
                finalDamage = if (hp - finalDamage <= 1) hp - 1f else finalDamage
                color = Color(128f, 255f, 0f, 1f)
            }
            else -> {
                color = Color(255f, 0f, 0f, 1f)
                println("Damage type \"$type\" does not exist!")
                return 0f
            }
        }
        damageColor = ColorUtils.colorInterpolation(damageColor, color, 1)
        damageColor = ColorUtils.applySaturation(damageColor, 0.8f)

        val damageNumber = Pools.get(DamageNumber::class.java).obtain()
        this.addActor(damageNumber)
        damageNumber.init(ColorUtils.toHexadecimal(damageColor), finalDamage)

        this.hp -= damage
        if (hp <= 0) {
            this.addAction(Actions.sequence(
                Actions.fadeOut(1.25f),
                Actions.removeActor()
            ))
            hp = 0f
            GlobalData.notifyObservers(GlobalDataType.CHARACTERDIED, this)
        }
        this.findActor<HpBar>("hpBar")?.update(hp)
        return damage
    }

    override fun getDamage(data: AttackData) {
        val evaded = Random.nextFloat() * (1 - data.accuracy + evasion)
        if (evaded != 0f && evaded in 0f .. evasion) {
            println("Evaded the attack")
            return
        }

        var damage: Float = 0f
        val physicalDamage = data.physicalDamage * data.physicalDamage / (data.physicalDamage + defence)
        val fireDamage = data.fireDamage * (1 - fireDefence)
        val waterDamage = data.waterDamage * (1 - waterDefence)
        val earthDamage = data.earthDamage * (1 - earthDefence)
        val airDamage = data.airDamage * (1 - airDefence)
        var poisonDamage = data.poisonDamage * (1 - poisonDefence)
        poisonDamage = if (hp - poisonDamage <= 1) hp - 1f else poisonDamage

        damage += physicalDamage
        damage += fireDamage
        damage += waterDamage
        damage += earthDamage
        damage += airDamage
        damage += poisonDamage

        if ((ai is EnemyAi && !(ai as EnemyAi).sensedEnemyArray.contains(data.character))) {
            println("Stealth hit!")
            val multiplier = data.character.getTag(CharacterTag.IncreaseStealthDamage::class)?.incrementPercent ?: 1f
            println("Multiplier: ${multiplier}")
            damage *= data.criticalDamage * multiplier
        }
        else if (Random.nextFloat() < data.criticalChance) {
            println("Critical hit!")
            damage *= data.criticalDamage
        }

        if (data.character.getTag(CharacterTag.Lifesteal::class) != null) {
            val heal = CharacterEvent(data.character,
                TimedEvent(1.0,
                    EventHeal(damage * data.character.getTag(CharacterTag.Lifesteal::class)!!.power)), Turn.turn)
            EventDispatcher.dispatchEvent(heal)
        }


        // get damage color from interpolation
        var damageColor: Color = Color(0f, 0f, 0f, 1f)
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(255f, 0f, 0f, 1f), (physicalDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(255f, 128f, 0f, 1f), (fireDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(0f, 0f, 255f, 1f), (waterDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(0f, 255f, 0f, 1f), (earthDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(0f, 255f, 255f, 1f), (airDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(128f, 255f, 0f, 1f), (poisonDamage / damage).toInt())

        damageColor = ColorUtils.applySaturation(damageColor, 0.8f)

        ActorVisuals.showDamage(this, damageColor, damage)

        if (ai is EnemyAi)
            (ai as EnemyAi).gotAttackedBy = data.character

        this.hp -= damage
        if (hp <= 0) {
            shaders.clear()
            this.addAction(Actions.sequence(
                Actions.fadeOut(1.25f),
                Actions.removeActor()
            ))
            hp = 0f
            GlobalData.notifyObservers(GlobalDataType.CHARACTERDIED, this)
        }
        this.findActor<HpBar>("hpBar").update(hp)
    }

    fun dropItems(): MutableList<Item> {
        return itemtemDropList
    }

    fun isAlive(): Boolean {
        return hp.compareDelta(0f) == 1
    }

    fun getPosition(): Coord {
        return Coord.get(xPos, yPos)
    }
}