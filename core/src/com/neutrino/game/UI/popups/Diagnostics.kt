package com.neutrino.game.UI.popups

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.GlobalData
import com.neutrino.GlobalDataObserver
import com.neutrino.GlobalDataType

class Diagnostics: Table() {
    private var time = System.nanoTime()
    private var totalTime: Long = 0

    val dungeonTypeLabel = TextraLabel("current dungeon", KnownFonts.getStandardFamily())
    private val fpsLabel = TextraLabel("fps", KnownFonts.getStandardFamily())
    private val memoryLabel = TextraLabel("memory", KnownFonts.getStandardFamily())
    private val renderLabel = TextraLabel("render time", KnownFonts.getStandardFamily())
    private val maxRenderTimeLabel = TextraLabel("max render time", KnownFonts.getStandardFamily())
    private val cameraPositionLabel = TextraLabel("current position", KnownFonts.getStandardFamily())

    init {
        dungeonTypeLabel.align = Align.left
        fpsLabel.align = Align.left
        memoryLabel.align = Align.left
        renderLabel.align = Align.left
        maxRenderTimeLabel.align = Align.left
        cameraPositionLabel.align = Align.left
        dungeonTypeLabel.wrap = true
        fpsLabel.wrap = true
        memoryLabel.wrap = true
        renderLabel.wrap = true
        maxRenderTimeLabel.wrap = true
        cameraPositionLabel.wrap = true
        dungeonTypeLabel.name = "dungeonTypeLabel"
        fpsLabel.name = "fpsLabel"
        memoryLabel.name = "memoryLabel"
        renderLabel.name = "renderLabel"
        maxRenderTimeLabel.name = "maxRenderLabel"
        cameraPositionLabel.name = "cameraPositionLabel"

        clip(true)
        align(Align.left)
        row().pad(4f)
        add(dungeonTypeLabel).left().width(190f)
        row().pad(4f)
        add(fpsLabel).left().width(190f)
        row().pad(4f)
        add(memoryLabel).left().width(190f)
        row().pad(4f)
        add(renderLabel).left().width(190f)
        row().pad(4f)
        add(maxRenderTimeLabel).left().width(190f)
        row().pad(4f)
        add(cameraPositionLabel).left().width(190f)
        name = "diagnostics"
        pack()

        GlobalData.registerObserverGetData(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.CHANGELEVEL
            override fun update(data: Any?): Boolean {
                if (data != null) {
                    setDungeonType(data as String)
                    return true
                }
                return false
            }
        })

    }
    private var maxRenderTime: Long = 0
    fun resetMaxTimes() {
        maxRenderTime = 0
    }
    fun setDungeonType(type: String) {
        dungeonTypeLabel.setText("[%75]$type")
    }
    private var cameraPositionText = ""
    fun updatePosition(x: Int, y: Int) {
        cameraPositionText = "[%75]Camera position: x: $x y: $y"
    }

    fun updateValues(renderStartTime: Long?) {
        if (!isVisible)
            return
        val timeDiff = System.nanoTime() - time
        totalTime += timeDiff
        time = System.nanoTime()
        if (renderStartTime != null) {
            if (System.nanoTime() - renderStartTime > maxRenderTime) {
                maxRenderTime = System.nanoTime() - renderStartTime
                maxRenderTimeLabel.setText("[%75]max render: ${maxRenderTime / 1000000f} ms")
            }
        }
        if (totalTime < 1000000000)
            return
        fpsLabel.setText("[%75]${Gdx.graphics.framesPerSecond} fps")
        memoryLabel.setText("[%75]${(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576} MB")
        cameraPositionLabel.setText(cameraPositionText)
        if (renderStartTime != null)
            renderLabel.setText("[%75]render: ${(System.nanoTime() - renderStartTime) / 1000000f} ms")
        totalTime = 0
    }


}