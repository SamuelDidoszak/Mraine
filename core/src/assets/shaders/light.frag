#version 130

#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_texCoord;
varying vec4 v_color;

uniform sampler2D u_texture;

void main() {

    int intensity = int(v_color.a * 255) % 25;
    // this is pointless, added here just because spriteBatch.draw always passes u_texture
    //     so it has to be implemented for the compiler not to optimize it away
    if (texture2D(u_texture, vec2(0,0)).a == 0.0) {
        gl_FragColor = vec4(intensity);
        return;
    }

    float distance = length(0.5 - v_texCoord) * 2.0;
    vec4 color = vec4(v_color.rgb, (0.7 * pow(intensity / 24.0, 0.6) + 0.1) * pow(0.1, distance) * pow(1.0 - distance, 2.0));

    gl_FragColor = color;
}