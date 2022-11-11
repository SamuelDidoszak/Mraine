package com.neutrino.game.domain.use_case.map.utility



data class GenerationParams(
    var difficulty: Float,
    private var _enemyMultiplier: Float = 1f,
    /** Quantity -> Quality slider, where <1 generates more weaker enemies and >1 generates less but stronger enemies */
    var enemyQuality: Float = 1f,
    var itemRarityMultiplier: Float = 1f,
    /** Multiplier for the total item value. 0 generates no items */
    private var _itemMultiplier: Float = 1f,
    /** Quantity -> Quality slider, where <1 splits items into more of lesser quality and >1 generates less but better items */
    var itemQuality: Float = 1f,
    var canGenerateOnTheFloor: Boolean = true
) {
    var enemyMultiplier: Float = _enemyMultiplier
        set(value) { field = if (value < 0f) 0f else value }
    var itemMultiplier: Float = _itemMultiplier
        set(value) { field = if (value < 0f) 0f else value }

    fun getTotalItemValue(): Float {
        return difficulty * 100 * itemMultiplier
    }

    fun mergeParams(generationParams: GenerationParams) {
        difficulty = (difficulty + generationParams.difficulty) / 2
        enemyMultiplier = (enemyMultiplier + generationParams.enemyMultiplier) / 2
        enemyQuality = (enemyQuality + generationParams.enemyQuality) / 2
        itemRarityMultiplier = (itemRarityMultiplier + generationParams.itemRarityMultiplier) / 2
        itemMultiplier = (itemMultiplier + generationParams.itemMultiplier) / 2
        itemQuality = (itemQuality + generationParams.itemQuality) / 2
    }

    fun mergeParamModifiers(generationParams: GenerationParams) {
        difficulty += generationParams.difficulty
        enemyMultiplier += generationParams.enemyMultiplier
        enemyQuality += generationParams.enemyQuality
        itemRarityMultiplier += generationParams.itemRarityMultiplier
        itemMultiplier += generationParams.itemMultiplier
        itemQuality += generationParams.itemQuality
    }
}
