#version 330

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

in vec3 in_position;
in vec2 in_textureCoords;
in vec3 in_normal;
in ivec3 in_jointIndices;
in vec3 in_weights;

uniform vec3 lightPos;
uniform float useFakeLighting;

uniform float numberOfRows;
uniform vec2 offset;

uniform mat4 jointTransforms[MAX_JOINTS];
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 transformationMatrix;

out vec2 passTextureCoords;
out vec3 unitNormal;
out vec3 unitVectorToCamera;
out vec3 toLightVector;
out float visibility;


const float density = 0.00245;
const float gradient = 2.85;

void main(void){

    vec4 totalLocalPos = vec4(0.0);
    vec4 totalNormal = vec4(0.0);

    for(int i=0;i<MAX_WEIGHTS;i++){
        mat4 jointTransform = jointTransforms[in_jointIndices[i]];
        vec4 posePosition = jointTransform *   vec4(in_position, 1.0);
        totalLocalPos += posePosition * in_weights[i];
        vec4 worldNormal = jointTransform * vec4(in_normal, 0.0);
        totalNormal += worldNormal * in_weights[i];
    }

    if (useFakeLighting == 1){
        totalNormal = vec4(0.0, 1.0, 0.0,0.0);
    }

    vec4 worldPosition = transformationMatrix * totalLocalPos;
    vec4 relativePositionToCamera =  viewMatrix * worldPosition;
    gl_Position = projectionMatrix * relativePositionToCamera;
    passTextureCoords = (in_textureCoords/numberOfRows)+offset;

    vec3 surfaceNormals = (transformationMatrix * totalNormal).xyz;

    vec3 toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

    for (int i = 0; i < 2; i++){
        toLightVector = lightPos - worldPosition.xyz;
    }
    unitNormal = normalize(surfaceNormals);
    unitVectorToCamera = normalize(toCameraVector);

    float distance = length(relativePositionToCamera.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);

}