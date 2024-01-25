package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.util.Constants

sealed class AiIntentionIcons {
    class ENEMY_DETECTED: AiIntentionIcons(), IntentionIcon {
        override val statusName: String = "enemy detected"
        override val statusTexture: TextureRegion = Constants.DefaultIconTexture.findRegion("exclamationMark")
        override val displayTime: Float = 0.5f
    }
    class WAITING: AiIntentionIcons(), IntentionIcon {
        override val statusName: String = "waiting"
        override val statusTexture: TextureRegion = Constants.DefaultIconTexture.findRegion("threeDots")
        override val displayTime: Float = 1f
    }
    class AFFECTION: AiIntentionIcons(), IntentionIcon {
        override val statusName: String = "tamed"
        override val statusTexture: TextureRegion = Constants.DefaultIconTexture.findRegion("heart")
        override val displayTime: Float = 1f
    }
}