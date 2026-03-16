package com.neutrino.game.entities.items.attributes.tags

enum class ItemTag {
    ALL,
    EQUIPMENT,
    // Weapons
    HANDHELD,
    TWOHANDED,
    // Melee
    SHIELD,
    MELEE,
    SWORD,
    DAGGER,
    AXE,
    SPEAR,

    // Ranged
    BOW,
    CROSSBOW,
    ARROW,

    // Magic
    WAND,
    STAFF,
    PARCHMENT,

    // Wearables
    HEAVY,
    LIGHTWEIGHT,

    HEAD,
    TORSO,
    LEGS,
    HANDS,
    FEET,

    AMULET,
    RING,
    BAG,

    // Edibles
    EDIBLE,
    FOOD,
    POTION,

    SKILLBOOK,
    SCROLL,

    // Others
    JUNK,
    SPECIAL, // Will not generate in chests nor will drop from enemies until stated as a drop
    FLOOR // Item can generate on the floor
}