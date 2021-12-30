package fr.ostix.worldCreator.core.resourcesProcessor;

import fr.ostix.worldCreator.core.loader.*;
import fr.ostix.worldCreator.core.resourcesProcessor.GLRequest;
import fr.ostix.worldCreator.entity.Entity;
import fr.ostix.worldCreator.entity.animated.colladaParser.dataStructures.*;
import fr.ostix.worldCreator.graphics.model.MeshModel;
import fr.ostix.worldCreator.graphics.model.Model;
import fr.ostix.worldCreator.graphics.model.ModelData;

public class ModelLoaderRequest extends GLRequest {

    private ModelData data;
    private AnimatedModelData animData;
    private boolean isAnimated;
    private MeshModel model;
    private String texture;


    public ModelLoaderRequest(AnimatedModelData data,String texture) {
        this.animData = data;
        this.data = new ModelData(data.getMeshData());
        this.isAnimated = true;
        this.texture = texture;
    }

    public ModelLoaderRequest(ModelData data){
        this.data = data;
    }

    public ModelLoaderRequest(ModelData data,String texture) {
        this.data = data;
        this.isAnimated = false;
        this.texture = texture;
    }

    public AnimatedModelData getAnimData() {
        return animData;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    @Override
    public void execute() {
        if (isAnimated){
          model = Loader.INSTANCE.loadToVAO(data.getIndices(), data.getVertices(),
       data.getTexcoords(), data.getNormals(), data.getJointsId(), data.getVertexWeights());
        }else{
            model = Loader.INSTANCE.loadToVAO(data.getVertices(),
                    data.getTexcoords(), data.getNormals(), data.getIndices());
        }

        super.execute();
    }

    public ModelData getData() {
        return data;
    }

    public String getTexture() {
        return texture;
    }

    public MeshModel getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "ModelLoaderRequest{" +
                "isExecuted=" + isExecuted +
                ", data=" + data +
                ", isAnimated=" + isAnimated +
                ", texture='" + texture + '\'' +
                '}';
    }
}
