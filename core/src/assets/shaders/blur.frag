#ifdef GL_ES
    precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

void main()
{
    float Pi = 6.28318530718; // Pi*2

    // GAUSSIAN BLUR SETTINGS {{{
    float Directions = 16.0; // BLUR DIRECTIONS (Default 16.0 - More is better but slower)
    float Quality = 3.0; // BLUR QUALITY (Default 4.0 - More is better but slower)
    float Size = 8.0; // BLUR SIZE (Radius)
    // GAUSSIAN BLUR SETTINGS }}}

    vec2 iResolution = vec2(1600.0, 900.0);

    vec2 Radius = Size/iResolution.xy;

    vec4 Color = texture2D(u_texture, v_texCoord).rgba;

    // Blur calculations
    for(float d = 0.0; d < Pi; d += Pi/Directions)
    {
        for(float i = 1.0 / Quality; i <= 1.0; i += 1.0 / Quality)
        {
            Color += texture2D(u_texture, v_texCoord + vec2(cos(d), sin(d)) * Radius * i);
        }
    }

    // Output to screen
    Color /= Quality * Directions - 15.0;
    gl_FragColor = Color;
}