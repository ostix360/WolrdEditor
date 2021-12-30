package fr.ostix.worldCreator.core.loader;

import org.joml.Vector3f;


public class LoadEntity {

    private static final Loader LOADER = Loader.INSTANCE;

    @SuppressWarnings("unchecked")
//    private static Entity loadAllProperties(String file, Model m) {
//        HashMap<String, Object> properties = new Yaml().load(
//                LoadEntity.class.getResourceAsStream("/model/" + file + "/" + file + ".yml"));
//        HashMap<String, Object> entityProperty = (HashMap<String, Object>) properties.get("entity");
//        Vector3f pos = parseVector3f((String) entityProperty.get("position"));
//        Vector3f rot = parseVector3f((String) entityProperty.get("rotation"));
//        float scale = Float.parseFloat(String.valueOf(entityProperty.get("scale")).trim());
//        HashMap<String, Object> boundingModelsProperties = (HashMap<String, Object>) properties.get("boundingBox");
//        if (boundingModelsProperties != null) {
//            BoundingModel[] boundingModels = new BoundingModel[boundingModelsProperties.size()];
//            for (int i = 0; i < boundingModelsProperties.size(); i++) {
//                HashMap<String, Object> boundingModel = (HashMap<String, Object>) boundingModelsProperties.get("b"+ i);
//                String modelName = (String) boundingModel.get("path");
//                MeshModel mM = LoadMeshModel.loadModel(file + "/" + modelName, LOADER);
//                Vector3f posM = parseVector3f((String) boundingModel.get("pos"));
//                Vector3f rotM = parseVector3f((String) boundingModel.get("rot"));
//                float scaleM = Float.parseFloat(String.valueOf(boundingModel.get("scale")).trim());
//                boundingModels[i] = new BoundingModel(mM, posM, rotM, scaleM);
//            }
//            return new Entity(m,pos,rot,scale,boundingModels);
//        }
//        return new Entity(m, pos, rot, scale);
//    }

    private static Vector3f parseVector3f(String s) {
        final String[] ss = s.split(",");
        return new Vector3f(
                Float.parseFloat(ss[0].trim()),
                Float.parseFloat(ss[1].trim()),
                Float.parseFloat(ss[2].trim()));
    }
}
