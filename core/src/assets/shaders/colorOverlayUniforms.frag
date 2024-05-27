#ifdef GL_ES
    #define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

#version 120

varying LOWP vec4 v_color;
varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform vec4 u_color;

void main()
{
    gl_FragColor = vec4(u_color.rgb, u_color.a * texture2D(u_texture, v_texCoord).a);
}