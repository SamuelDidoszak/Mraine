package com.neutrino.game.util

object SeedUtil {

    private fun splitMix64(seed: Long): Long {
        var z = seed + 0x9E3779B97F4A7C15UL.toLong()
        z = (z xor (z ushr 30)) * 0xBF58476D1CE4E5B9UL.toLong()
        z = (z xor (z ushr 27)) * 0x94D049BB133111EBUL.toLong()
        return z xor (z ushr 31)
    }

    fun branch(seed: Long, key: String): Long {
        var keyHash = seed
        for (c in key) {
            keyHash = keyHash * 31 + c.code
        }
        return splitMix64(seed xor keyHash)
    }
}