
import com.neutrino.game.entities.Characters
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.attributes.*
import com.neutrino.game.entities.characters.attributes.util.FactionEnum
import com.neutrino.game.entities.characters.attributes.util.LootElement
import com.neutrino.game.entities.shared.attributes.RandomizationSimple
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.drawing.layers.CharacterInfoGroup
import com.neutrino.game.graphics.textures.Textures
import kotlin.random.Random

Characters.add("Mouse") {
    Character()
        .addAttribute(Stats(
            hpMax = 13f,
            strength = 2f,
            dexterity = 2f,
            intelligence = 0f,
            luck = 2f,
            damageMin = 1f,
            damageMax = 2f,
            defence = 0.5f,
            criticalChance = 0.05f
        ))
        .addAttribute(EnemyAi(viewDistance = 8))
        .addAttribute(Faction(FactionEnum.ENEMY))
        .addAttribute(Texture { position, random, textures ->
            textures.add(Textures.get("mouse_idle"))
        })
        .addAttribute(CharacterTags())
        .addAttribute(CharacterInfoGroup())
        .addAttribute(Loot(LootElement(
            "meat",
            {entity: Entity, rng: Random -> entity.get(RandomizationSimple::class)?.randomize(rng) },
            0.25f)))
}
Characters.add("Slime") {
    Character()
        .addAttribute(Stats(
            hpMax = 25f,
            strength = 3f,
            damageMin = 1f,
            damageMax = 4f,
            poisonDamageMin = 0f,
            poisonDamageMax = 2f,
            defence = 1.5f,
            criticalChance = 0.05f
        ))
        .addAttribute(EnemyAi(viewDistance = 8))
        .addAttribute(Faction(FactionEnum.ENEMY))
        .addAttribute(Texture { position, random, textures ->
            textures.add(Textures.get("Slime_idle"))
        })
        .addAttribute(CharacterTags())
        .addAttribute(CharacterInfoGroup())
}






















