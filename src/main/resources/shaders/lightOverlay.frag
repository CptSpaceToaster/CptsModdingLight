#version 120

uniform sampler2D Texture;
uniform sampler2D LightMap;
varying vec2 p_TexCoord;
varying vec4 p_Color;
varying vec4 p_LightCoord;
uniform vec4 u_LightCoord;

void main() {
    float scale = 256;
    float bias = 0.5;

    // required state:
    // LightMap GL_TEXTURE_MIN_FILTER GL_LINEAR
    //	    GL_TEXTURE_MAG_FILTER GL_LINEAR
    //	    GL_TEXTURE_WRAP_S	  GL_CLAMP_TO_EDGE
    //	    GL_TEXTURE_WRAP_T	  GL_CLAMP_TO_EDGE

    // exploit separability of *-linear interpolation:

    // hardware does the bilinear interpolation for the zw channels
    vec4 texel00 = texture2D(LightMap, (floor(p_LightCoord.xy + vec2(0, 0)) * 16 + p_LightCoord.zw) / scale);
    vec4 texel01 = texture2D(LightMap, (floor(p_LightCoord.xy + vec2(0, 1)) * 16 + p_LightCoord.zw) / scale);
    vec4 texel10 = texture2D(LightMap, (floor(p_LightCoord.xy + vec2(1, 0)) * 16 + p_LightCoord.zw) / scale);
    vec4 texel11 = texture2D(LightMap, (floor(p_LightCoord.xy + vec2(1, 1)) * 16 + p_LightCoord.zw) / scale);

    vec2 factors = fract(p_LightCoord.xy);

    // do the y interpolation steps ourselves
    vec4 y0 = mix(texel00, texel01, factors.y);
    vec4 y1 = mix(texel10, texel11, factors.y);

    // finally do the x step between the y results
    vec4 lightColor = mix(y0, y1, factors.x);

    gl_FragColor = texture2D(Texture, p_TexCoord) * p_Color * lightColor;
}