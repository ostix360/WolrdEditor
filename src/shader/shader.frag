#version 330 core

const int MAX_LIGHT = 5;

in vec2 passTextureCoords;
in vec3 unitNormal;
in vec3 unitVectorToCamera;
in vec3 toLightVector[MAX_LIGHT];
in float visibility;

uniform sampler2D textureSampler;
uniform sampler2D normalMap;
uniform sampler2D specularMap;

uniform float useSpecularMap;
uniform vec3 lightColor[MAX_LIGHT];
uniform vec3 attenuation[MAX_LIGHT];
uniform float lightPower[MAX_LIGHT];
uniform float reflectivity;
uniform float shine;

uniform vec3 filters;

uniform vec3 skyColor;

out vec4 out_Color;

void main(){
    vec4 normalMapValue = 2 * texture(normalMap, passTextureCoords)-1.0;
    vec3 unitNormals = unitNormal;
    if (normalMapValue.a > 5){
        unitNormals = normalize(normalMapValue.rgb);
    }

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular= vec3(0.0);
    for (int i = 0;i<MAX_LIGHT;i++){
        float distance = length(toLightVector[i]);
        float attenuationFactor = max(attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance), 1.0);

        vec3 unitLightVector = normalize(toLightVector[i]);

        float nDotl = dot(unitNormals, unitLightVector);

        float brightness = max(nDotl,0.0);

        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormals);

        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shine);
        vec3 specular = dampedFactor * lightColor[i] * reflectivity;

        totalDiffuse = totalDiffuse + (brightness * lightColor[i] * lightPower[i])/attenuationFactor;
        totalSpecular = totalSpecular + max(vec3(0.), (dampedFactor * lightColor[i] * reflectivity))/attenuationFactor;
    }

    if(useSpecularMap > 0.5){
        vec4 mapinfo = texture(specularMap,passTextureCoords);
        totalSpecular *= mapinfo.r;
        if (mapinfo.g > 0.5){
            totalDiffuse = vec3(1.0);
        }
    }

    vec4 textureColor = texture(textureSampler, passTextureCoords);
    if (textureColor.a < 0.5){
        discard;
    }
    totalDiffuse = clamp(totalDiffuse, 0.2, 1.0);

    out_Color =  vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
    out_Color =  mix(vec4(skyColor, 1.0), out_Color, visibility);
    out_Color = mix(out_Color,vec4(filters,1.0),0.5);
}