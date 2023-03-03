#ifdef GL_ES
    precision mediump float;
#endif

#version 120

varying vec4 v_color;
varying vec2 v_texCoord;

uniform sampler2D u_texture;
uniform mat4 u_projTrans;

void main() {
    vec4 color = texture2D(u_texture, v_texCoord).rgba;
    float gray = (color.r + color.g + color.b) / 3.0;
    vec3 grayscale = vec3(gray);

    gl_FragColor = vec4(grayscale, color.a);
}