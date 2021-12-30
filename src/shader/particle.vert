#version 330

in vec2 position;
in mat4 modelViewMatrix;
in vec4 texOffsets;
in float blendFactor;

out vec2 textureCoords1;
out vec2 textureCoords2;
out float blend;

uniform mat4 projectionMatrix;

uniform float numberOfRows;

void main(void){

    vec2 texCoords = position + vec2(0.5, 0.5);
    texCoords.y = 1.0 - texCoords.y;
    texCoords /= numberOfRows;
    textureCoords1 = texCoords + texOffsets.xy;
    textureCoords2 = texCoords + texOffsets.zw;
    blend = blendFactor;

    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);

}