package fr.ostix.worldCreator.entity.animated.colladaParser.colladaLoader;

import fr.ostix.worldCreator.entity.animated.colladaParser.dataStructures.*;
import fr.ostix.worldCreator.entity.animated.colladaParser.xmlParser.*;
import org.joml.*;
import org.lwjgl.*;

import java.lang.Math;
import java.nio.*;
import java.util.*;


public class SkeletonLoader {

    private final XmlNode armatureData;

    private final List<String> boneOrder;

    private int jointCount = 0;

    private static final Matrix4f CORRECTION = new Matrix4f().rotate((float) Math.toRadians(0), new Vector3f(1, 0, 0));

    public SkeletonLoader(XmlNode visualSceneNode, List<String> boneOrder) {
        this.armatureData = visualSceneNode.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");
        this.boneOrder = boneOrder;
    }

    public SkeletonData extractBoneData() {
        XmlNode headNode = armatureData.getChild("node");
        JointData headJoint = loadJointData(headNode, true);
        return new SkeletonData(jointCount, headJoint);
    }

    private JointData loadJointData(XmlNode jointNode, boolean isRoot) {
        JointData joint = extractMainJointData(jointNode, isRoot);
        for (XmlNode childNode : jointNode.getChildren("node")) {
            joint.addChild(loadJointData(childNode, false));
        }
        return joint;
    }

    private JointData extractMainJointData(XmlNode jointNode, boolean isRoot) {
        String nameId = jointNode.getAttribute("sid");
        int index = boneOrder.indexOf(nameId);
        String[] matrixData = jointNode.getChild("matrix").getData().split(" ");
        Matrix4f matrix = new Matrix4f();
        matrix.set(convertData(matrixData));
        matrix.transpose();
        if (isRoot) {
            //because in Blender z is up, but in our game y is up.
            CORRECTION.mul(matrix);
        }
        jointCount++;
        return new JointData(index, nameId, matrix);
    }

    private FloatBuffer convertData(String[] rawData) {
        float[] matrixData = new float[16];
        for (int i = 0; i < matrixData.length; i++) {
            matrixData[i] = Float.parseFloat(rawData[i]);
        }
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(matrixData);
        buffer.flip();
        return buffer;
    }

}
