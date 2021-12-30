#version 400 core

in vec3 textureCoords;

layout(location = 0) out vec4 out_Color;
layout(location = 1) out vec4 out_BrightColor;

uniform samplerCube cubeMap;
uniform samplerCube cubeMap2;
uniform float blendFactor;
uniform vec3 fogColor;

const float lowerLimit = -10.0;
const float upperLimit = 40.0;

void main(void){
    vec4 texture1 = texture(cubeMap, textureCoords);
    vec4 texture2 = texture(cubeMap2, textureCoords);
    vec4 finalColor = mix(texture1, texture2, blendFactor);

    float fogFactor = (textureCoords.y+lowerLimit)/(upperLimit-lowerLimit);
    fogFactor = clamp(fogFactor, 0.0, 1.0);
    out_Color = mix(vec4(fogColor, 1.0), finalColor, fogFactor);
    out_BrightColor = vec4(0.27);
}