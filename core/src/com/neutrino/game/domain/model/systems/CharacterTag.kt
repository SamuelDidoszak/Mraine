package com.neutrino.game.domain.model.systems

sealed interface CharacterTag {
    /** ======================================================================================================================================================
                                                                    Strength based
    */
    class IncreaseOnehandedDamage(
        var incrementPercent: Float
    ): CharacterTag

    class IncreaseTwohandedDamage(
        var incrementPercent: Float
    ): CharacterTag

    class IncreaseBlockingDefence(
        var incrementPercent: Float
    ): CharacterTag

    class IncreaseDefence(
        var incrementPercent: Float
    ): CharacterTag

    /** ======================================================================================================================================================
                                                                    Dexterity based
    */

    class IncreaseStealth(
        var incrementPercent: Float
    ): CharacterTag

    class IncreaseStealthDamage(
        var incrementPercent: Float
    ): CharacterTag

    class IncreaseEvasionOnWait(
        var incrementPercent: Float
    ): CharacterTag


    /** ======================================================================================================================================================
                                                                    Intelligence based
    */

    class ReduceManaCost(
        var reducePercent: Float
    ): CharacterTag


    /** ======================================================================================================================================================
                                                                    Others
    */
    class Lifesteal(
        var power: Float
    ): CharacterTag

    class ReduceCooldown(
        var reducePercent: Float
    ): CharacterTag
}