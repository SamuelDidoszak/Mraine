#version 130

#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_texCoord;

uniform sampler2D u_texture;

uniform vec4 u_outlineColor;
uniform vec2 u_pixelSize;

uniform vec4 u_texBoundaries;

void alternativeAlgorithm() {
    if (texture2D(u_texture, v_texCoord).a == 0.0) {
        vec2 coord;
        for (int y = -1; y < 2; y++) {
            coord.y = v_texCoord.y + y * u_pixelSize.y;
            if (coord.y < u_texBoundaries.y && y != 1)
                coord.y += u_pixelSize.y;
            else if (coord.y > u_texBoundaries.w && y != -1)
                coord.y -= u_pixelSize.y;

            for (int x = -1; x < 2; x++) {
                coord.x = v_texCoord.x + x * u_pixelSize.x;
                if (coord.x < u_texBoundaries.x && x != 1)
                    coord.x += u_pixelSize.x;
                else if (coord.x > u_texBoundaries.z && x != -1)
                    coord.x -= u_pixelSize.x;

                if (texture2D(u_texture, coord).a != 0.0) {
                    gl_FragColor = u_outlineColor;
                    return;
                }
            }
        }
    }

    gl_FragColor = vec4(0.0);
}

void main() {
    if (texture2D(u_texture, v_texCoord).a == 0.0) {
        vec2 coord;
        for (int y = -1; y < 2; y++) {
            coord.y = v_texCoord.y + y * u_pixelSize.y;
            if (coord.y < u_texBoundaries.y || coord.y > u_texBoundaries.w)
                continue;
            for (int x = -1; x < 2; x++) {
                coord.x = v_texCoord.x + x * u_pixelSize.x;
                if (coord.x < u_texBoundaries.x || coord.x > u_texBoundaries.z)
                    continue;

                if (texture2D(u_texture, coord).a != 0.0) {
                    gl_FragColor = u_outlineColor;
                    return;
                }
            }
        }
    }

    gl_FragColor = vec4(0.0);
}