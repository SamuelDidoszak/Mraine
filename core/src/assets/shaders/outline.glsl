#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_texCoord;

uniform sampler2D u_texture;

uniform vec4 u_outlineColor;
uniform vec2 u_textureSize;

void main() {
    if (texture2D(u_texture, v_texCoord).a == 0.0) {
        vec2 pixelSize = 1.0 / u_textureSize;
        if (
            texture2D(u_texture, vec2(v_texCoord.x - pixelSize.x, v_texCoord.y)).a > 0.5 ||
            texture2D(u_texture, vec2(v_texCoord.x + pixelSize.x, v_texCoord.y)).a > 0.5 ||
            texture2D(u_texture, vec2(v_texCoord.x, v_texCoord.y + pixelSize.y)).a > 0.5 ||
            texture2D(u_texture, vec2(v_texCoord.x, v_texCoord.y - pixelSize.y)).a > 0.5) {
            gl_FragColor = u_outlineColor;
            return;
        }

//        for (int y = -1; y < 2; y++) {
//            for (int x = -1; x < 2; x++) {
//                if (texture2D(u_texture, vec2(v_texCoords.x + x * pixelSize.x, v_texCoords.y + y * pixelSize.y)).a != 0.0) {
//                    gl_FragColor = vec4(0.75, 0.25, 0.75, 1.0);
////                    gl_FragColor = u_outlineColor;
//                    return;
//                }
//            }
//        }
    }

    gl_FragColor = vec4(0.0);
}