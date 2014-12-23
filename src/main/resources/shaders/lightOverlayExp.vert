#version 120

uniform sampler2D Texture;
uniform sampler2D LightMap;
uniform vec4 u_LightCoord;
attribute vec2 TexCoord;
varying vec2 p_TexCoord;
attribute vec4 LightCoord;
varying vec4 p_LightCoord;
varying vec4 p_Color;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    p_TexCoord = TexCoord;
    p_Color = gl_Color;
    if (u_LightCoord == vec4(0.0, 0.0, 0.0, 0.0)) {
        p_LightCoord = LightCoord;
    } else {
        p_LightCoord = u_LightCoord;
    }
}