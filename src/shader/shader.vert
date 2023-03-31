#version 330 core

const int MAX_LIGHT = 5;

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

in vec3 position;
in vec2 textureCoords;
in vec3 normals;
in ivec3 jointIndices;
in vec3 weights;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPos[MAX_LIGHT];


uniform float isAnimated;
uniform float useFakeLighting;

uniform mat4 jointTransforms[MAX_JOINTS];
uniform float numberOfRows;
uniform vec2 offset;
uniform vec4 clipPlane;

out vec2 passTextureCoords;
out vec3 unitNormal;
out vec3 unitVectorToCamera;
out vec3 toLightVector[MAX_LIGHT];
out float visibility;


const float density = 0.00095;
const float gradient = 5.8;

void main(){

    vec4 totalLocalPos = vec4(0.0);
    vec4 totalNormal = vec4(0.0);

    if (isAnimated > 0.5){
        for (int i=0;i<MAX_WEIGHTS;i++){
            mat4 jointTransform = jointTransforms[jointIndices[i]];
            vec4 posePosition = jointTransform *   vec4(position, 1.0);
            totalLocalPos += posePosition * weights[i];
            vec4 worldNormal = jointTransform * vec4(normals, 0.0);
            totalNormal += worldNormal * weights[i];
        }
    } else {
        totalLocalPos = vec4(position, 1.0);
        totalNormal = vec4(normals, 0.0);
    }
    if (useFakeLighting == 1){
        totalNormal = vec4(0.0, 1.0, 0.0, 0.0);
    }

    vec4 worldPosition = transformationMatrix * totalLocalPos;

    gl_ClipDistance[0] = dot(worldPosition, clipPlane);

    vec4 relativePositionToCamera =  viewMatrix * worldPosition;
    gl_Position = projectionMatrix * relativePositionToCamera;
    passTextureCoords = (textureCoords/numberOfRows)+offset;

    vec3 surfaceNormals = (transformationMatrix * totalNormal).xyz;

    vec3 toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

    for (int i = 0; i < MAX_LIGHT; i++){
        toLightVector[i] = lightPos[i] - worldPosition.xyz;
    }
    unitNormal = normalize(surfaceNormals);
    unitVectorToCamera = normalize(toCameraVector);

    float distance = length(relativePositionToCamera.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}