package fr.ostix.worldCreator.entity.animated.animation.loaders;


import fr.ostix.worldCreator.entity.animated.animation.animation.*;
import fr.ostix.worldCreator.entity.animated.colladaParser.colladaLoader.*;
import fr.ostix.worldCreator.entity.animated.colladaParser.dataStructures.*;
import org.joml.*;

import java.util.*;


/**
 * This class loads up an animation collada file, gets the information from it,
 * and then creates and returns an {@link Animation} from the extracted data.
 *
 * @author Karl
 */
public class AnimationLoader {

    /**
     * Loads up a collada animation file, and returns and animation created from
     * the extracted animation data from the file.
     *
     * @param colladaFile - the collada file containing data about the desired
     *                    animation.
     * @return The animation made from the data in the file.
     */
    public static Animation loadAnimation(String colladaFile) {
        AnimationData animationData = ColladaLoader.loadColladaAnimation(colladaFile);
        KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = createKeyFrame(animationData.keyFrames[i]);
        }
        return new Animation(animationData.lengthSeconds, frames);
    }

    /**
     * Creates a keyframe from the data extracted from the collada file.
     *
     * @param data - the data about the keyframe that was extracted from the
     *             collada file.
     * @return The keyframe.
     */
    private static KeyFrame createKeyFrame(KeyFrameData data) {
        HashMap<String, JointTransform> map = new HashMap<>();
        for (JointTransformData jointData : data.jointTransforms) {
            JointTransform jointTransform = createTransform(jointData);
            map.put(jointData.jointNameId, jointTransform);
        }
        return new KeyFrame(data.time, map);
    }

    /**
     * Creates a joint transform from the data extracted from the collada file.
     *
     * @param data - the data from the collada file.
     * @return The joint transform.
     */
    private static JointTransform createTransform(JointTransformData data) {
        Matrix4f mat = data.jointLocalTransform;
        Vector3f translation = new Vector3f(mat.m30(), mat.m31(), mat.m32());
        Quaternion rotation = Quaternion.fromMatrix(mat);
        return new JointTransform(translation, rotation);
    }

}
