package com.neutrino.game.domain.use_case

import com.neutrino.game.graphics.shaders.ShaderParametered

interface Shaderable {
    var shaders: ArrayList<ShaderParametered?>
}