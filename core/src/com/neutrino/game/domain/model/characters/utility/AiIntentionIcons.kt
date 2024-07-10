package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.graphics.textures.TextureSprite
import com.neutrino.game.graphics.textures.Textures

sealed class AiIntentionIcons {
    class ENEMY_DETECTED: AiIntentionIcons(), IntentionIcon {
        override val statusName: String = "enemy detected"
        override val statusTexture: TextureSprite = Textures.get("exclamationMark")
        override val displayTime: Float = 0.5f
    }
    class WAITING: AiIntentionIcons(), IntentionIcon {
        override val statusName: String = "waiting"
        override val statusTexture: TextureSprite = Textures.get("threeDots")
        override val displayTime: Float = 1f
    }
    class AFFECTION: AiIntentionIcons(), IntentionIcon {
        override val statusName: String = "tamed"
        override val statusTexture: TextureSprite = Textures.get("heart")
        override val displayTime: Float = 1f
    }
}