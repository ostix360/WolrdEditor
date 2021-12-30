#version 330

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform vec3 color;
uniform sampler2D textureAtlas;

const float width = 0.41;
const float edge = 0.3;

const float borderWidth = 0.53;
const float borderEdge = 0.36;

const vec2 offsets = vec2(0.006, 0.006);

const vec3 outlineColor = vec3(0.1, 0.1, 0.1);

float smoothlyStep(float edge0, float edge1, float x){
    float t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    return t * t * (3.0 - 2.0 * t);
}
void main(void){

    float distance = 1.0 - texture(textureAtlas, pass_textureCoords).a;
    float alpha = 1.0 - smoothlyStep(width, width + edge, distance);

    float distance2 = 1.0 - texture(textureAtlas, pass_textureCoords + offsets).a;
    float outlineAlpha = 1.0 - smoothlyStep(borderWidth, borderWidth + borderEdge, distance2);

    float overwallAlpha = alpha + (1.0-alpha) * outlineAlpha;
    vec3 overwallColor = mix(outlineColor, color, alpha/outlineAlpha);

    out_Color = vec4(overwallColor, overwallAlpha);
}