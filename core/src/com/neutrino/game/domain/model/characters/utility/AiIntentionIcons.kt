package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.graphics.textures.Textures

sealed class AiIntentionIcons {
    class ENEMY_DETECTED: AiIntentionIcons(), IntentionIcon {
        override val statusName: String = "enemy detected"
        override val statusTexture: TextureRegion = Textures.get("exclamationMark").texture
        override val displayTime: Float = 0.5f
    }
    class WAITING: AiIntentionIcons(), IntentionIcon {
        override val statusName: String = "waiting"
        override val statusTexture: TextureRegion = Textures.get("threeDots").texture
        override val displayTime: Float = 1f
    }
    class AFFECTION: AiIntentionIcons(), IntentionIcon {
        override val statusName: String = "tamed"
        override val statusTexture: TextureRegion = Textures.get("heart").texture
        override val displayTime: Float = 1f
    }
}