package com.neutrino.game.utility

import com.neutrino.game.util.Constants

/**
 * Utilities for boolean arrays
 */
object BArray {
    /**
     * Default boolean array filled with false for faster copying
     */
    private val falseArray: BooleanArray = BooleanArray(Constants.LevelChunkSize) {false}

    /**
     * Sets provided array to false. Size has to be LevelChunkSize
     */
    fun setFalse(array: Array<BooleanArray>) {
        for (xArray in array)
            System.arraycopy(falseArray, 0, xArray, 0, falseArray.size)
    }
}