
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.Items
import com.neutrino.game.entities.characters.attributes.OffensiveStats
import com.neutrino.game.entities.items.Item
import com.neutrino.game.entities.items.attributes.*
import com.neutrino.game.entities.items.callables.AmountChangedCallable
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.entities.shared.attributes.Randomization
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.shared.util.InteractionType
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
        .addAttribute(Texture { position, random, textures -> run {
            textures add Textures.get("dagger")
        }})
        .addAttribute(HandheldEquipment(
            HandheldEquipmentType.DAGGER,
            EquipmentType.RHAND
        ))
        .addAttribute(OffensiveStats(
            damageMin = 2f,
            damageMax = 3.5f,
        ))
        .addAttribute(GoldValue(25))
        .addAttribute(Interaction(arrayListOf(InteractionType.ITEM())))
}