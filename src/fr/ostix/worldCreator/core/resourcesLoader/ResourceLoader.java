package fr.ostix.worldCreator.core.resourcesLoader;


import fr.ostix.worldCreator.core.resourcesProcessor.*;
import fr.ostix.worldCreator.entity.animated.animation.loaders.*;

public class ResourceLoader {


    public static ModelLoaderRequest loadTexturedModel(String path, String texture) {
        if (texture == null) {
            new IllegalArgumentException("texture for the model " + path + "is not available ");
        }
        assert texture != null;
        return new ModelLoaderRequest(LoadMeshModel.loadModel(path), texture);
    }

    public static ModelLoaderRequest loadTexturedAnimatedModel(String path, String texture) {
        if (texture == null) {
            new IllegalArgumentException("texture for the model " + path + "is not available ");
        }
        assert texture != null;
        return new ModelLoaderRequest(AnimatedModelLoader.loadMeshData(path,  3 ),texture);
    }
}
