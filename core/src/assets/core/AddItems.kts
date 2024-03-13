
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.Items
import com.neutrino.game.entities.characters.attributes.OffensiveStats
import com.neutrino.game.entities.items.Item
import com.neutrino.game.entities.items.attributes.*
import com.neutrino.game.entities.systems.events.callables.AddCooldown
import com.neutrino.game.entities.items.attributes.usable.EquipEvents
import com.neutrino.game.entities.items.attributes.usable.UseEvents
import com.neutrino.game.entities.items.attributes.usable.UseOnEntity
import com.neutrino.game.entities.items.callables.AmountChangedCallable
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.entities.shared.attributes.Randomization
import com.neutrino.game.entities.shared.attributes.RandomizationSimple
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.shared.util.InteractionType
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Cooldown
import com.neutrino.game.entities.systems.events.TimedEvent
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.util.add
import kotlin.math.roundToInt

Items.add("Gold") {
    Item()
        .addAttribute(Texture { position, random, textures -> run {
            textures add Textures.get("gold1")
        }})
        .addAttribute(Amount(maxStack = Int.MAX_VALUE))
        .addAttribute(GoldValue(1))
        .addAttribute(ItemTier(1))
        .addAttribute(Interaction(arrayListOf(InteractionType.ITEM())))
        .addAttribute(Randomization { rng, quality, difficulty, entity -> run {
            val randomAmount = rng.nextFloat() * (difficulty * 5) * quality
            entity.get(Amount::class)!!.amount = randomAmount.roundToInt()
        } })
        .attach(object : AmountChangedCallable() {
            override fun call(entity: Entity, vararg data: Any?): Boolean {
                val amount = data[0] as Int
                val textureName =
                    if (amount < 10) "gold1"
                    else if (amount < 20) "gold2"
                    else if (amount < 50) "gold3"
                    else if (amount < 100) "gold4"
                    else if (amount < 200) "gold5"
                    else if (amount < 400) "gold6"
                    else if (amount < 700) "gold7"
                    else "gold8"
                if (entity has Position::class) {
                    entity.get(Texture::class)!!.textures.clear()
                    entity.get(Texture::class)!!.textures add (Textures get textureName)
                }
                return true
            }
        })
}
Items.add("Dagger") {
    Item()
        .addAttribute(EquipmentInitializer(
            textureName = "dagger",
            goldValue = 25,
            eqType = EquipmentType.RHAND,
            handheldType = HandheldEquipmentType.DAGGER
        ))
        .addAttribute(OffensiveStats(
            damageMin = 2f,
            damageMax = 3.5f,
        ))
        .addAttribute(EquipEvents(TimedEvent(CharacterEvents.Heal(2f), 2.0, 10)))
}
Items.add("Sword") {
    Item()
        .addAttribute(EquipmentInitializer(
            textureName = "sword",
            goldValue = 30,
            eqType = EquipmentType.RHAND,
            handheldType = HandheldEquipmentType.SWORD
        ))
        .addAttribute(OffensiveStats(
            damageMin = 3f,
            damageMax = 4f,
        ))
}
Items.add("Meat") {
    Item()
        .addAttribute(ItemInitializer(
            textureName = "meat",
            goldValue = 5,
            maxStack = 10,
            tier = 1
        ))
        .addAttribute(UseOnEntity(true))
        .addAttribute(UseEvents(
            TimedEvent.TimedData(0.2f, 0.5, 60).toTimedEvent {
                CharacterEvents.Heal(it.power) }
        ))
        .attach(AddCooldown(Cooldown.Type.FOOD, CharacterEvents.Heal::class))
        .addAttribute(RandomizationSimple {rng, entity ->
            val heal = entity.get(UseEvents::class)!!.getEvent(CharacterEvents.Heal::class)!!
            rng.nextFloat().let {
                when {
                    it < 0.2 -> {
                        entity.name = "Rotten meat"
                        heal.power = 0.15f
                        entity.get(GoldValue::class)!!.value -= 2
                    }
                    it < 0.4 -> {
                        entity.name = "Tasty meat"
                        heal.power = 0.3f
                        entity.get(GoldValue::class)!!.value += 3
                    } } }
        })
}
Items.add("Small healing potion") {
    Item()
        .addAttribute(ItemInitializer(
            "smallHealingPotion",
            15,
            10,
            2
        ))
        .addAttribute(UseOnEntity(true))
        .addAttribute(UseEvents(
            TimedEvent(CharacterEvents.Heal(20f))
        ))
        .addAttribute(RandomizationSimple { rng, entity ->
            val heal = entity.get(UseEvents::class)!!.getEvent(CharacterEvents.Heal::class)!!
            rng.nextDouble().let {
                when {
                    it < 0.6 -> {
                        heal.power = 15f
                        entity.name = "Diluted small healing potion"
                        entity.get(GoldValue::class)!!.value -= 5
                    }
                    it < 0.7 -> {
                        heal.power = 25f
                        entity.name = "Concentrated small healing potion"
                        entity.get(GoldValue::class)!!.value += 7
                    } } }
        })
}










