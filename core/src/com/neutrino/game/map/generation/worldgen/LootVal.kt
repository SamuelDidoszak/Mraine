package com.neutrino.game.map.generation.worldgen

import com.neutrino.game.entities.items.attributes.tags.ItemTag

sealed class LootVal(val probability: Float) {

    class Item(
        val name: String,
        probability: Float
    ) : LootVal(probability)

    class Type(
        val query: ItemQuery,
        probability: Float
    ) : LootVal(probability) {
        constructor(query: String, probability: Float) : this(
            parseQuery(query),
            probability
        )

        companion object {
            private enum class Mode {
                REQUIRED,
                BLOCKED,
                PREFERRED
            }

            private fun parseQuery(raw: String): ItemQuery {
                val required = mutableSetOf<ItemTag>()
                val blocked = mutableSetOf<ItemTag>()
                val preferred = mutableMapOf<ItemTag, Float>()

                var mode = Mode.REQUIRED

                val tokens = raw.split(" ")

                for (token in tokens) {
                    when (token) {
                        "+" -> mode = Mode.REQUIRED
                        "-" -> mode = Mode.BLOCKED
                        "/" -> mode = Mode.PREFERRED
                        else -> {
                            when (mode) {
                                Mode.REQUIRED -> {
                                    required += ItemTag.valueOf(token.uppercase())
                                }

                                Mode.BLOCKED -> {
                                    blocked += ItemTag.valueOf(token.uppercase())
                                }

                                Mode.PREFERRED -> {
                                    val (tag, bonus) = parsePreferred(token)
                                    preferred[tag] = bonus
                                }
                            }
                        }
                    }
                }

                return ItemQuery(
                    requiredTags = required,
                    preferredTags = if (preferred.isEmpty()) null else preferred,
                    blockedTags = if (blocked.isEmpty()) null else blocked,
                    tier = null
                )
            }

            private fun parsePreferred(token: String): Pair<ItemTag, Float> {

                val tagPart = token.takeWhile { !it.isDigit() }
                val numberPart = token.dropWhile { !it.isDigit() }

                val tag = ItemTag.valueOf(tagPart.uppercase())

                val bonus = if (numberPart.isEmpty()) {
                    0.1f
                } else {
                    numberPart.toFloat() / 10f
                }

                return tag to bonus
            }
        }
    }
}