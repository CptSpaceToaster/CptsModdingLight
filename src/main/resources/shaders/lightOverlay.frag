#version 120

uniform sampler2D Texture;
uniform sampler2D LightMap;
varying vec2 p_TexCoord;
varying vec4 p_Color;
varying vec4 p_LightCoord;
uniform ivec4 u_LightCoord;

void main() {
    float scale = 256;
    float bias = 0.5;
    vec4 texel0000 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 0)) * 16 + (floor(p_LightCoord.zw + vec2(0, 0)) + bias)) / scale, 0, 1));
    vec4 texel0001 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 0)) * 16 + (floor(p_LightCoord.zw + vec2(0, 1)) + bias)) / scale, 0, 1));
    vec4 texel0010 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 0)) * 16 + (floor(p_LightCoord.zw + vec2(1, 0)) + bias)) / scale, 0, 1));
    vec4 texel0011 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 0)) * 16 + (floor(p_LightCoord.zw + vec2(1, 1)) + bias)) / scale, 0, 1));
    vec4 texel0100 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 1)) * 16 + (floor(p_LightCoord.zw + vec2(0, 0)) + bias)) / scale, 0, 1));
    vec4 texel0101 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 1)) * 16 + (floor(p_LightCoord.zw + vec2(0, 1)) + bias)) / scale, 0, 1));
    vec4 texel0110 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 1)) * 16 + (floor(p_LightCoord.zw + vec2(1, 0)) + bias)) / scale, 0, 1));
    vec4 texel0111 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 1)) * 16 + (floor(p_LightCoord.zw + vec2(1, 1)) + bias)) / scale, 0, 1));
    vec4 texel1000 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 0)) * 16 + (floor(p_LightCoord.zw + vec2(0, 0)) + bias)) / scale, 0, 1));
    vec4 texel1001 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 0)) * 16 + (floor(p_LightCoord.zw + vec2(0, 1)) + bias)) / scale, 0, 1));
    vec4 texel1010 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 0)) * 16 + (floor(p_LightCoord.zw + vec2(1, 0)) + bias)) / scale, 0, 1));
    vec4 texel1011 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 0)) * 16 + (floor(p_LightCoord.zw + vec2(1, 1)) + bias)) / scale, 0, 1));
    vec4 texel1100 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 1)) * 16 + (floor(p_LightCoord.zw + vec2(0, 0)) + bias)) / scale, 0, 1));
    vec4 texel1101 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 1)) * 16 + (floor(p_LightCoord.zw + vec2(0, 1)) + bias)) / scale, 0, 1));
    vec4 texel1110 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 1)) * 16 + (floor(p_LightCoord.zw + vec2(1, 0)) + bias)) / scale, 0, 1));
    vec4 texel1111 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 1)) * 16 + (floor(p_LightCoord.zw + vec2(1, 1)) + bias)) / scale, 0, 1));
    vec4 lightColor =   texel0000 * (1 - fract(p_LightCoord.x)) * (1 - fract(p_LightCoord.y)) * (1 - fract(p_LightCoord.z)) * (1 - fract(p_LightCoord.w)) +
                        texel0001 * (1 - fract(p_LightCoord.x)) * (1 - fract(p_LightCoord.y)) * (1 - fract(p_LightCoord.z)) * fract(p_LightCoord.w) +
                        texel0010 * (1 - fract(p_LightCoord.x)) * (1 - fract(p_LightCoord.y)) * fract(p_LightCoord.z) * (1 - fract(p_LightCoord.w)) +
                        texel0011 * (1 - fract(p_LightCoord.x)) * (1 - fract(p_LightCoord.y)) * fract(p_LightCoord.z) * fract(p_LightCoord.w) +
                        texel0100 * (1 - fract(p_LightCoord.x)) * fract(p_LightCoord.y) * (1 - fract(p_LightCoord.z)) * (1 - fract(p_LightCoord.w)) +
                        texel0101 * (1 - fract(p_LightCoord.x)) * fract(p_LightCoord.y) * (1 - fract(p_LightCoord.z)) * fract(p_LightCoord.w) +
                        texel0110 * (1 - fract(p_LightCoord.x)) * fract(p_LightCoord.y) * fract(p_LightCoord.z) * (1 - fract(p_LightCoord.w)) +
                        texel0111 * (1 - fract(p_LightCoord.x)) * fract(p_LightCoord.y) * fract(p_LightCoord.z) * fract(p_LightCoord.w) +
                        texel1000 * fract(p_LightCoord.x) * (1 - fract(p_LightCoord.y)) * (1 - fract(p_LightCoord.z)) * (1 - fract(p_LightCoord.w)) +
                        texel1001 * fract(p_LightCoord.x) * (1 - fract(p_LightCoord.y)) * (1 - fract(p_LightCoord.z)) * fract(p_LightCoord.w) +
                        texel1010 * fract(p_LightCoord.x) * (1 - fract(p_LightCoord.y)) * fract(p_LightCoord.z) * (1 - fract(p_LightCoord.w)) +
                        texel1011 * fract(p_LightCoord.x) * (1 - fract(p_LightCoord.y)) * fract(p_LightCoord.z) * fract(p_LightCoord.w) +
                        texel1100 * fract(p_LightCoord.x) * fract(p_LightCoord.y) * (1 - fract(p_LightCoord.z)) * (1 - fract(p_LightCoord.w)) +
                        texel1101 * fract(p_LightCoord.x) * fract(p_LightCoord.y) * (1 - fract(p_LightCoord.z)) * fract(p_LightCoord.w) +
                        texel1110 * fract(p_LightCoord.x) * fract(p_LightCoord.y) * fract(p_LightCoord.z) * (1 - fract(p_LightCoord.w)) +
                        texel1111 * fract(p_LightCoord.x) * fract(p_LightCoord.y) * fract(p_LightCoord.z) * fract(p_LightCoord.w);
    gl_FragColor = texture2D(Texture, p_TexCoord) * p_Color * lightColor;
}