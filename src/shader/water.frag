#version 330 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCameraVector;
layout(location = 0) out vec4 out_Color;
layout(location = 1) out vec4 out_BrightColor;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;
uniform vec3 lightColor;
uniform vec3 lightDirection;

uniform float moveFactor;

const float waveStrength = 0.01;
const float shineDamper = 10.0;
const float reflectivity = 0.8;

void main(void) {
    vec2 ndc = (clipSpace.xy/clipSpace.w)/2+0.5;
    vec2 reflectionTexCoords = vec2(ndc.x, -ndc.y);
    vec2 refractionTexCoords = vec2(ndc.x, ndc.y);

    float near = 0.1;
    float far = 1000.0;
    float depth = texture(depthMap, refractionTexCoords).r;
    float floorDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));

    depth = gl_FragCoord.z;
    float waterDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
    float waterDepth = floorDistance - waterDistance;


    vec2 distortedTexCoords = texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg*0.1;
    distortedTexCoords = textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+moveFactor);
    vec2 totalDistortion = (texture(dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength;//* clamp(waterDepth/20.0, 0.0, 1.0);

    reflectionTexCoords += totalDistortion;
    reflectionTexCoords.x = clamp(reflectionTexCoords.x, 0.001, 0.999);
    reflectionTexCoords.y = clamp(reflectionTexCoords.y, -0.999, -0.001);

    refractionTexCoords += totalDistortion;
    refractionTexCoords = clamp(refractionTexCoords, 0.001, 0.999);

    vec4 reflectionColor = texture(reflectionTexture, reflectionTexCoords);
    vec4 refractionColor = texture(refractionTexture, refractionTexCoords);

    vec4 normalMapColor = texture(normalMap, distortedTexCoords);
    vec3 normal = vec3(normalMapColor.r * 2 -1, normalMapColor.b *3.0, normalMapColor.g * 2 -1);
    normal = normalize(normal);

    vec3 viewVector = normalize(toCameraVector);
    float refractiveFactor = dot(viewVector, normal);
    refractiveFactor = pow(refractiveFactor, 0.25);
    refractiveFactor = clamp(refractiveFactor, 0.0, 1.0);

    vec3 reflectedLight = reflect(normalize(lightDirection), normal);
    float specular = max(dot(reflectedLight, viewVector), 0.0);
    specular = pow(specular, shineDamper);
    vec3 specularHighlights = lightColor * specular * reflectivity;

    out_Color = mix(reflectionColor, refractionColor, refractiveFactor);
    out_Color = mix(out_Color, vec4(0.2, 0.2, 0.38, 1.0), 0.2) + vec4(specularHighlights, 0.0);
    out_Color.a = clamp(waterDepth/5.0, 0.0, 1.0);
    out_BrightColor = vec4(0.0);
}