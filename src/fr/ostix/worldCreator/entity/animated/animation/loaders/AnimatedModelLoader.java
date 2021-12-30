package fr.ostix.worldCreator.entity.animated.animation.loaders;



import fr.ostix.worldCreator.entity.animated.animation.animatedModel.*;
import fr.ostix.worldCreator.entity.animated.colladaParser.colladaLoader.*;
import fr.ostix.worldCreator.entity.animated.colladaParser.dataStructures.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.graphics.textures.*;
import fr.ostix.worldCreator.toolBox.*;

public class AnimatedModelLoader {

    /**
     * Creates an AnimatedEntity from the data in an entity file. It loads up
     * the collada model data, stores the extracted data in a VAO, sets up the
     * joint heirarchy, and loads up the entity's texture.
     *
     * @param model - the meshModel of the entity.
     * @return The animated entity (no animation applied though)
     */
    public static AnimatedModel loadEntity(MeshModel model,Texture texture, SkeletonData skeletonData) {
        Joint headJoint = createJoints(skeletonData.headJoint);
        return new AnimatedModel(model, texture, headJoint, skeletonData.jointCount);
    }

    public static AnimatedModelData loadMeshData(String modelFile, int maxWeights){
        AnimatedModelData entityData = ColladaLoader.loadColladaModel(Config.REPOSITORY_FOLDER + "/models/entities/" + modelFile + ".dae", maxWeights);
        return new AnimatedModelData(entityData.getMeshData(),entityData.getJointsData());

    }

    /**
     * Constructs the joint-hierarchy skeleton from the data extracted from the
     * collada file.
     *
     * @param data - the joints data from the collada file for the head joint.
     * @return The created joint, with all its descendants added.
     */
    private static Joint createJoints(JointData data) {
        Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
        for (JointData child : data.children) {
            joint.addChild(createJoints(child));
        }
        return joint;
    }



}
