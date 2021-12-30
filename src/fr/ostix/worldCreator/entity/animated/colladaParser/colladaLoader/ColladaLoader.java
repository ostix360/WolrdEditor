package fr.ostix.worldCreator.entity.animated.colladaParser.colladaLoader;


import fr.ostix.worldCreator.entity.animated.colladaParser.dataStructures.*;
import fr.ostix.worldCreator.entity.animated.colladaParser.xmlParser.*;

public class ColladaLoader {

    public static AnimatedModelData loadColladaModel(String path, int maxWeights) {
        XmlNode node = XmlParser.loadXmlFile(path);

        SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
        SkinningData skinningData = skinLoader.extractSkinData();

        SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
        SkeletonData jointsData = jointsLoader.extractBoneData();

        GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
        MeshData meshData = g.extractModelData();

        return new AnimatedModelData(meshData, jointsData);
    }

    public static AnimationData loadColladaAnimation(String colladaFile) {
        XmlNode node = XmlParser.loadXmlFile(colladaFile);
        XmlNode animNode = node.getChild("library_animations");
        XmlNode jointsNode = node.getChild("library_visual_scenes");
        AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
        AnimationData animData = loader.extractAnimation();
        return animData;
    }

}
