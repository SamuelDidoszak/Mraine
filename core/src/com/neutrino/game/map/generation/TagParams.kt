package com.neutrino.game.map.generation

data class TagParams(
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

    fun mergeParams(tagParams: TagParams) {
        difficulty = (difficulty + tagParams.difficulty) / 2
        enemyMultiplier = (enemyMultiplier + tagParams.enemyMultiplier) / 2
        enemyQuality = (enemyQuality + tagParams.enemyQuality) / 2
        itemRarityMultiplier = (itemRarityMultiplier + tagParams.itemRarityMultiplier) / 2
        itemMultiplier = (itemMultiplier + tagParams.itemMultiplier) / 2
        itemQuality = (itemQuality + tagParams.itemQuality) / 2
    }

    fun mergeParamModifiers(tagParams: TagParams) {
        difficulty += tagParams.difficulty
        enemyMultiplier += tagParams.enemyMultiplier
        enemyQuality += tagParams.enemyQuality
        itemRarityMultiplier += tagParams.itemRarityMultiplier
        itemMultiplier += tagParams.itemMultiplier
        itemQuality += tagParams.itemQuality
    }
}
