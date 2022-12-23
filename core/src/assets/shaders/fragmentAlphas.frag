#ifdef GL_ES
    #define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoord;
uniform sampler2D u_texture;

void main()
{
    vec4 texColor = texture2D(u_texture, v_texCoord);
    if (texColor.a > 0.39 && texColor.a < 0.786)
        texColor.a = 1.0;
    gl_FragColor = v_color * texColor;
}