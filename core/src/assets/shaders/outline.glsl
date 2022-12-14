#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_texCoord;

uniform sampler2D u_texture;

uniform vec4 u_outlineColor;
uniform vec2 u_pixelSize;

void main() {
    if (texture2D(u_texture, v_texCoord).a == 0.0) {
        vec2 coord;
        for (int y = -1; y < 2; y++) {
            for (int x = -1; x < 2; x++) {
                coord = v_texCoord + vec2(x * u_pixelSize.x, y * u_pixelSize.y);
                if (texture2D(u_texture, coord).a != 0.0) {
                    gl_FragColor = u_outlineColor;
                    return;
                }
            }
        }
//        if (
//            texture2D(u_texture, vec2(v_texCoord.x - pixelSize.x, v_texCoord.y + pixelSize.y)).a > 0.0 ||
//            texture2D(u_texture, vec2(v_texCoord.x, v_texCoord.y + pixelSize.y)).a > 0.0 ||
//            texture2D(u_texture, vec2(v_texCoord.x + pixelSize.x, v_texCoord.y + pixelSize.y)).a > 0.0 ||
//
//            texture2D(u_texture, vec2(v_texCoord.x - pixelSize.x, v_texCoord.y)).a > 0.0 ||
//            texture2D(u_texture, vec2(v_texCoord.x, v_texCoord.y)).a > 0.0 ||
//            texture2D(u_texture, vec2(v_texCoord.x + pixelSize.x, v_texCoord.y)).a > 0.0 ||
//
//            texture2D(u_texture, vec2(v_texCoord.x - pixelSize.x, v_texCoord.y - pixelSize.y)).a > 0.0 ||
//            texture2D(u_texture, vec2(v_texCoord.x, v_texCoord.y - pixelSize.y)).a > 0.0 ||
//            texture2D(u_texture, vec2(v_texCoord.x + pixelSize.x, v_texCoord.y - pixelSize.y)).a > 0.0) {
//            gl_FragColor = u_outlineColor;
//            return;
//        }
    }

    gl_FragColor = vec4(0.0);
}