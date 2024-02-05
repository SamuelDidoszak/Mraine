
import com.neutrino.game.entities.Characters
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.CharacterTags
import com.neutrino.game.entities.characters.attributes.EnemyAi
import com.neutrino.game.entities.characters.attributes.Faction
import com.neutrino.game.entities.characters.attributes.Stats
import com.neutrino.game.entities.characters.attributes.util.FactionEnum

Characters.add("Rat") {
    Entity()
        .addAttribute(Stats(
            hpMax = 13f,
            mpMax = 10f,
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
        .addAttribute(CharacterTags())
}






















