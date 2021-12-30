package fr.ostix.worldCreator.core.resourcesLoader;


import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.toolBox.*;
import org.joml.*;
import org.lwjgl.assimp.*;

import java.util.*;
import java.lang.Math;

public class LoadMeshModel {
    private static final Matrix4f CORRECTION = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));


    public static ModelData loadModel(String fileName) {
        AIScene scene = Assimp.aiImportFile(Config.REPOSITORY_FOLDER + "/models/entities/" + fileName + ".obj",
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_CalcTangentSpace |
                        Assimp.aiProcess_LimitBoneWeights |
                        Assimp.aiProcess_GenSmoothNormals |
                        Assimp.aiProcess_FlipUVs
        );


        if (scene == null) {
            System.err.println("the imported file does not contain any scene. " + Config.REPOSITORY_FOLDER + "/models/entities/" + fileName + ".obj");
            //System.exit(0);
        }

        assert scene != null;
        AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(0));


        float[] pos = new float[mesh.mNumVertices() * 3];
        float[] texCoords = new float[mesh.mNumVertices() * 2];
        float[] normals = new float[mesh.mNumVertices() * 3];
        int[] indices = new int[mesh.mNumFaces() * mesh.mFaces().get(0).mNumIndices()];

        int indexPos = 0;
        int indexTex = 0;
        int indexNorm = 0;

        for (int v = 0; v < mesh.mNumVertices(); v++) {
            AIVector3D position = mesh.mVertices().get(v);
            AIVector3D normal = Objects.requireNonNull(mesh.mNormals()).get(v);
            AIVector3D texCoord = Objects.requireNonNull(mesh.mTextureCoords(0)).get(v);

            /*
             * The above assumes that the program has texture coordinates, if it doesn't the program will throw a null pointer exception.
             */

            pos[indexPos++] = position.x();
            pos[indexPos++] = position.y();
            pos[indexPos++] = position.z();

            texCoords[indexTex++] = texCoord.x();
            texCoords[indexTex++] = texCoord.y();

            normals[indexNorm++] = normal.x();
            normals[indexNorm++] = normal.y();
            normals[indexNorm++] = normal.z();

        }

        int index = 0;

        for (int f = 0; f < mesh.mNumFaces(); f++) {
            AIFace face = mesh.mFaces().get(f);
            for (int ind = 0; ind < face.mNumIndices(); ind++) {
                indices[index++] = (face.mIndices().get(ind));
            }
        }
        return new ModelData(pos, texCoords, indices, normals);
    }
//
//
//    public static AnimatedModel loadModel(String fileName, Texture tex, Loader loader) {
//        System.out.println(fileName);
//        AIScene scene = Assimp.aiImportFile(RES_FOLDER + "\\model\\" + fileName + ".dae",
//                Assimp.aiProcess_Triangulate |
//                        Assimp.aiProcess_GenSmoothNormals |
//                        Assimp.aiProcess_FlipUVs |
//                        Assimp.aiProcess_CalcTangentSpace |
//                        Assimp.aiProcess_LimitBoneWeights
//        );
//
//
//        if (scene == null) {
//            System.err.println("the imported file does not contain any animations.");
//            //System.exit(0);
//        }
//
//        assert scene != null;
//        AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(0));
//
//
//        float[] pos = new float[mesh.mNumVertices() * 3];
//        float[] texCoords = new float[mesh.mNumVertices() * 2];
//        float[] normals = new float[mesh.mNumVertices() * 3];
//        int[] indices = new int[mesh.mNumFaces() * mesh.mFaces().get(0).mNumIndices()];
//
//        int indexPos = 0;
//        int indexTex = 0;
//        int indexNorm = 0;
//
//        for (int v = 0; v < mesh.mNumVertices(); v++) {
//            AIVector3D position = mesh.mVertices().get(v);
//            AIVector3D normal = Objects.requireNonNull(mesh.mNormals()).get(v);
//            AIVector3D texCoord = Objects.requireNonNull(mesh.mTextureCoords(0)).get(v);
//
//            /*
//             * The above assumes that the program has texture coordinates, if it doesn't the program will throw a null pointer exception.
//             */
//
//            pos[indexPos++] = position.x();
//            pos[indexPos++] = position.y();
//            pos[indexPos++] = position.z();
//
//            texCoords[indexTex++] = texCoord.x();
//            texCoords[indexTex++] = texCoord.y();
//
//            normals[indexNorm++] = normal.x();
//            normals[indexNorm++] = normal.y();
//            normals[indexNorm++] = normal.z();
//
//        }
//
//        int index = 0;
//
//        for (int f = 0; f < mesh.mNumFaces(); f++) {
//            AIFace face = mesh.mFaces().get(f);
//            for (int ind = 0; ind < face.mNumIndices(); ind++) {
//                indices[index++] = (face.mIndices().get(ind));
//            }
//        }
//
//        HashMap<String, Integer> boneMap = new HashMap<>();
//        HashMap<Integer, Integer> bone_index_map0 = new HashMap<>();
//        HashMap<Integer, Integer> bone_index_map1 = new HashMap<>();
//
//        int[] boneIndex = new int[4 * mesh.mNumVertices()];
//        float[] weightIndex = new float[4 * mesh.mNumVertices()];
//
//        int sizeOfVertexUnrigged = 0;
//        int sizeOfVertex = 4;
//
//        for (int b = 0; b < mesh.mNumBones(); b++) {
//            AIBone bone = AIBone.create(Objects.requireNonNull(mesh.mBones()).get(b));
//            boneMap.put(bone.mName().dataString(), b);
//            System.out.println("...");
//
//            for (int w = 0; w < bone.mNumWeights(); w++) {
//                AIVertexWeight weight = bone.mWeights().get(w);
//                int vertexIndex = weight.mVertexId();
//                int findex = vertexIndex * sizeOfVertex;
//
//                if (!bone_index_map0.containsKey(vertexIndex)) {
//                    boneIndex[(findex + sizeOfVertexUnrigged)] = b;
//                    weightIndex[(findex + sizeOfVertexUnrigged) + 2] = weight.mWeight();
//                    bone_index_map0.put(vertexIndex, 0);
//                } else if (bone_index_map0.get(vertexIndex) == 0) {
//                    boneIndex[(findex + sizeOfVertexUnrigged) + 1] = b;
//                    weightIndex[(findex + sizeOfVertexUnrigged) + 3] = weight.mWeight();
//                    bone_index_map0.put(vertexIndex, 1);
//                } else if (!bone_index_map1.containsKey(vertexIndex)) {
//                    boneIndex[(findex + sizeOfVertexUnrigged) + 4] = b;
//                    weightIndex[(findex + sizeOfVertexUnrigged) + 6] = weight.mWeight();
//                    bone_index_map1.put(vertexIndex, 0);
//                } else if (bone_index_map1.get(vertexIndex) == 0) {
//                    boneIndex[(findex + sizeOfVertexUnrigged) + 5] = b;
//                    weightIndex[(findex + sizeOfVertexUnrigged) + 7] = weight.mWeight();
//                    bone_index_map1.put(vertexIndex, 1);
//                } else {
//                    System.err.println("max 4 bones per vertex.");
//                    System.exit(0);
//                }
//            }
//        }
//
//        AIMatrix4x4 inverseRootTransform = Objects.requireNonNull(scene.mRootNode()).mTransformation();
//        Matrix4f inverseRootTransformation = Maths.fromAssimpMatrix(inverseRootTransform);
//
//
//        JointData[] bones = new JointData[boneMap.size()];
//
//        for (int b = 0; b < mesh.mNumBones(); b++) {
//            AIBone bone = AIBone.create(Objects.requireNonNull(mesh.mBones()).get(b));
//            String name = bone.mName().dataString();
//            System.out.println(name);
//            Matrix4f transform = Maths.fromAssimpMatrix(bone.mOffsetMatrix());
//            bones[b] = new JointData(b, name, transform);
//        }
//
//        JointData jointDataRoot = new JointData(bones[0].index, bones[0].nameId, CORRECTION.mul(inverseRootTransformation));
//        for (int i = 1; i < bones.length; i++) {
//            jointDataRoot.addChild(bones[i]);
//        }
//
//        Joint jointRoot = createJoints(jointDataRoot);
//
//        return new AnimatedModel(loader.loadToVAO(indices, pos, texCoords, normals, boneIndex, weightIndex),
//                tex, jointRoot, 3);
//    }
//
//    private static Joint createJoints(JointData data) {
//        Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
//        for (JointData child : data.children) {
//            joint.addChild(createJoints(child));
//        }
//        return joint;
//    }


}
