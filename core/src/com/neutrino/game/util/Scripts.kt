package com.neutrino.game.util

import com.badlogic.gdx.Gdx
import com.neutrino.game.util.Constants.scriptEngine

class Scripts {

    fun evaluate() {
//        scriptEngine.importAll(getImportList())
        scriptEngine.evaluate(Gdx.files.internal("core/AddTextures.kts"))
        scriptEngine.evaluate(Gdx.files.internal("core/AddEntities.kts"))
        scriptEngine.evaluate(Gdx.files.internal("core/AddTilesets.kts"))
        scriptEngine.evaluate(Gdx.files.internal("core/AddGenerators.kts"))
        scriptEngine.evaluate(Gdx.files.internal("core/AddGenerationRequirements.kts"))
    }

    private fun getImportList(): List<String> {
        return listOf("com.neutrino.entities.*",
            "com.neutrino.game.entities.characters.attributes.*",
            "com.neutrino.game.entities.items.attributes.*",
            "com.neutrino.game.entities.map.attributes.*",
            "com.neutrino.game.entities.shared.attributes.*",
            "com.neutrino.game.graphics.textures.Textures", "com.neutrino.game.graphics.textures.TextureSprite",
            "com.neutrino.game.graphics.textures.AnimatedTextureSprite",
            "com.neutrino.game.map.generation.util.NameOrIdentity",
            "com.neutrino.game.map.generation.Tilesets",
            "com.neutrino.game.map.generation.Generators",
            "com.neutrino.game.map.generation.algorithms.*",
            "squidpony.squidgrid.mapping.styled.TilesetType",
            "com.neutrino.game.map.generation.*",

        )
    }
}