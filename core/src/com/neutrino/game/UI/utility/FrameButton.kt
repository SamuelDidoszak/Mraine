package com.neutrino.game.UI.utility

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.util.Fonts
import com.neutrino.game.util.setTextSameWidth
import ktx.actors.*
import ktx.scene2d.Scene2DSkin

class FrameButton(
    text: String,
    disabled: Boolean = false,
    width: Float? = null,
    height: Float? = null,
    private val normalTextColor: String = "[#663931]",
    private val mouseOverTextColor: String = normalTextColor,
    private val pressedTextColor: String = "[#45251e]",
    private val disabledTextColor: String = "[#47342c]",
    private val onClick: () -> Unit
): Table() {

    private val textraLabel = TextraLabel("$normalTextColor$text", Fonts.EQUIPMENT, Color.BLACK)
    var text: String = text
        set(value) {
            field = value
            val hasColor = value.substring(0, 2) == "[#"
            textraLabel.setTextSameWidth(if (hasColor) value else normalTextColor + value)
        }
    var disabled: Boolean = disabled
        set(value) {
            field = value
            if (field) {
                background = disabledTexture
                setColor(Type.Disabled)
            }
        }

    private var yPosition = 0
    private fun resetPosition() {
        textraLabel.y -= yPosition
        yPosition = 0
    }
    private fun moveBy(pixels: Int) {
        resetPosition()
        textraLabel.y += pixels
        yPosition += pixels
    }

    var normalTexture = Scene2DSkin.defaultSkin.getDrawable("buttonFrame")
    var mouseOverTexture = Scene2DSkin.defaultSkin.getDrawable("buttonOverFrame")
    var pressedTexture = Scene2DSkin.defaultSkin.getDrawable("buttonPressedFrame")
    var disabledTexture = Scene2DSkin.defaultSkin.getDrawable("buttonDisabledFrame")

    private enum class Type {
        MouseOver,
        Normal,
        Pressed,
        Disabled
    }

    private fun setColor(type: Type) {
        val color = when (type) {
            Type.MouseOver -> mouseOverTextColor
            Type.Normal -> normalTextColor
            Type.Pressed -> pressedTextColor
            Type.Disabled -> disabledTextColor
        }
        textraLabel.setTextSameWidth("$color$text")
    }

    init {
        background = normalTexture
        name = "${text.substringAfterLast(']')}Button"
        add(textraLabel).fill().center()

        if (width != null)
            this.width = width
        if (height != null)
            this.height = height
        this.disabled = disabled

        onEnter {
            if (this.disabled)
                return@onEnter
            background = mouseOverTexture
            moveBy(2)
        }
        onExit {
            if (this.disabled)
                return@onExit
            background = normalTexture
            setColor(Type.Normal)
            resetPosition()
        }
        onTouchDown {
            if (this.disabled)
                return@onTouchDown
            background = pressedTexture
            setColor(Type.Pressed)
            moveBy(-3)
        }
        onTouchUp {
            if (this.disabled)
                return@onTouchUp
            background = normalTexture
            setColor(Type.Normal)
            resetPosition()
        }
        onClick {
            if (this.disabled)
                return@onClick
            onClick.invoke()
        }
    }
}