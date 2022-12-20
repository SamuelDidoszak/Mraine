#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_texCoord;
varying vec4 v_color;

uniform sampler2D u_texture;

uniform float u_intensity;
uniform float U_flickering;

void main() {
    // this is pointless, added here just because spriteBatch.draw always passes u_texture
    //     so it has to be implemented for the compiler not to optimize it away
    if (texture2D(u_texture, vec2(0,0)) == 0.0) {
        gl_FragColor = vec4(0.0);
        return;
    }

    float distance = length(0.5 - v_texCoord);
    vec4 color = vec4(v_color.rgb, pow(0.05 * (u_intensity/24), distance) * (1 - pow(1.0 / u_intensity, u_intensity) * 2 / 3) - 0.002 * u_intensity);

    gl_FragColor = color;
}