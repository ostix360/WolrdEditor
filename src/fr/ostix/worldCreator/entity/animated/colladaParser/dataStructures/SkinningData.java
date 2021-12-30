package fr.ostix.worldCreator.entity.animated.colladaParser.dataStructures;

import java.util.*;

public class SkinningData {

    public final List<String> jointOrder;
    public final List<VertexSkinData> verticesSkinData;

    public SkinningData(List<String> jointOrder, List<VertexSkinData> verticesSkinData) {
        this.jointOrder = jointOrder;
        this.verticesSkinData = verticesSkinData;
    }


}
