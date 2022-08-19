package com.neutrino.game.domain.model.characters.utility

interface Stats {
    val hp: Float
    var currentHp: Float
    val mp: Float
    var currentMp: Float
    val strength: Float
    val defence: Float
    val agility: Float
    val evasiveness: Float
    val accuracy: Float
    val criticalChance: Float
    val luck: Float
    val attackSpeed: Float
    val movementSpeed: Float
}