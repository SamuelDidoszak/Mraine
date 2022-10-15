package com.neutrino.game.domain.model.utility

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel

class Diagnostics: Table() {
    private var time = System.nanoTime()
    private var totalTime: Long = 0

    private val fpsLabel = TextraLabel("fps", KnownFonts.getStandardFamily())
    private val memoryLabel = TextraLabel("memory", KnownFonts.getStandardFamily())
    private val renderLabel = TextraLabel("render time", KnownFonts.getStandardFamily())

    init {
        fpsLabel.align = Align.left
        memoryLabel.align = Align.left
        renderLabel.align = Align.left
        fpsLabel.wrap = true
        memoryLabel.wrap = true
        renderLabel.wrap = true
        fpsLabel.name = "fpsLabel"
        memoryLabel.name = "memoryLabel"
        renderLabel.name = "renderLabel"

        clip(true)
        align(Align.left)
        row().pad(4f)
        add(fpsLabel).left().width(168f)
        row().pad(4f)
        add(memoryLabel).left().width(168f)
        row().pad(4f)
        add(renderLabel).left().width(168f)
        name = "diagnostics"
        pack()
    }

    fun updateValues(renderStartTime: Long?) {
        if (!isVisible)
            return
        val timeDiff = System.nanoTime() - time
        totalTime += timeDiff
        time = System.nanoTime()
        if (totalTime < 1000000000)
            return
        fpsLabel.setText("[%75]${Gdx.graphics.framesPerSecond} fps")
        memoryLabel.setText("[%75]${(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576} MB")
        if (renderStartTime != null)
            renderLabel.setText("[%75]render: ${(System.nanoTime() - renderStartTime) / 1000000f} ms")
        totalTime = 0
    }


}