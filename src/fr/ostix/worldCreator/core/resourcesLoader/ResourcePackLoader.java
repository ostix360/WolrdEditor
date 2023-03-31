package fr.ostix.worldCreator.core.resourcesLoader;


import fr.ostix.worldCreator.audio.*;
import fr.ostix.worldCreator.core.Timer;
import fr.ostix.worldCreator.core.loader.json.*;
import fr.ostix.worldCreator.core.resourcesProcessor.*;
import fr.ostix.worldCreator.core.ressources.*;
import fr.ostix.worldCreator.entity.animated.animation.animatedModel.*;
import fr.ostix.worldCreator.entity.animated.animation.animation.*;
import fr.ostix.worldCreator.entity.animated.animation.loaders.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.graphics.textures.*;
import fr.ostix.worldCreator.toolBox.*;

import java.io.*;
import java.util.*;


public class ResourcePackLoader {

    private final String DATA = "data";
    private final HashMap<String, Texture> textureByName = new HashMap<>();
    private final HashMap<String, SoundSource> soundByName = new HashMap<>();
    private static final HashMap<String, Model> modelByName = new HashMap<>();
    private final HashMap<TextureLoaderRequest, String> textureRequestByName = new HashMap<>();
    private final HashMap<SoundRequest, String> soundRequestByName = new HashMap<>();
    private final HashMap<ModelLoaderRequest, String> modelRequestByName = new HashMap<>();
    private final HashMap<String, AnimatedModel> animatedModelByName = new HashMap<>();
    private final HashMap<AnimatedModel, HashMap<String, Animation>> animationByName = new HashMap<>();
    private static final HashMap<Integer, String> componentsByID = new HashMap<>();

    private static boolean isLoaded = false;

    public ResourcePackLoader() {
    }

    public void loadAllResource() throws Exception {

        loadAllTextures();

        loadAllSounds();

        loadAllModels();


        loadAllComponents();

        forceRequest();

        loadAllAnimations();
        isLoaded = true;
    }

    private void forceRequest() throws InterruptedException {
        GLRequestProcessor.forceRequest();
        Thread.sleep(100);
        while (GLRequestProcessor.isRunning()) {
            Thread.sleep(1);
        }
        for (TextureLoaderRequest texture : textureRequestByName.keySet()) {
            textureByName.put(textureRequestByName.get(texture), texture.getTex());
        }
        for (ModelLoaderRequest model : modelRequestByName.keySet()) {
            if (model.isAnimated()) {
                animatedModelByName.put(modelRequestByName.get(model), AnimatedModelLoader.
                        loadEntity(model.getModel(), textureByName.
                                get(model.getTexture()), model.getAnimData().getJointsData()));
            }
            modelByName.put(modelRequestByName.get(model), new Model(model.getModel(), textureByName.
                    get(model.getTexture())));
        }
        for (SoundRequest sound : soundRequestByName.keySet()) {
            soundByName.put(soundRequestByName.get(sound), sound.getSound());
        }
    }

    private void loadAllComponents() throws Exception {
        final File components = new File(Config.REPOSITORY_FOLDER, "/component/");
        for (File currentFile : Objects.requireNonNull(components.listFiles())) {
            String name = currentFile.getName();

            String fileContent = JsonUtils.loadJson(currentFile.getAbsolutePath());
            if (fileContent.isEmpty()) {
                throw new Exception("a json component is empty... " + currentFile.getAbsolutePath());
            }
            componentsByID.put(Integer.parseInt(name.replaceAll(".component", "")), fileContent);
        }
    }

    private void loadAllTextures() throws Exception {
        File textureFolder = new File(Config.REPOSITORY_FOLDER + "/textures/", DATA);
        List<File> files = new ArrayList<>();
        FileUtils.listFile(textureFolder, files);
        for (File currentFile : files) {

            String json = JsonUtils.loadJson(currentFile.getAbsolutePath());
            if (json.isEmpty()) {
                throw new Exception("a json a texture is empty... " + currentFile.getAbsolutePath());
            }
            TextureResources current = JsonUtils.gsonInstance().fromJson(json, TextureResources.class);
            if (current == null) {
                throw new NullPointerException("The file cannot " + currentFile.getName() + " be read");
            }
            String name = current.getName();
            TextureProperties prop = current.getTextureProperties();
            if (prop.getNormalMapName() != null) {
                TextureLoaderRequest tr = new TextureLoaderRequest("entities/normal/" + prop.getNormalMapName());
                GLRequestProcessor.sendRequest(tr);
                Timer.waitForRequest(tr);
                prop.setNormalMapID(tr.getTexture().getId());
            }
            if (prop.getSpecularMapName() != null) {
                TextureLoaderRequest tr = new TextureLoaderRequest("entities/specularMap/" + prop.getSpecularMapName());
                GLRequestProcessor.sendRequest(tr);
                Timer.waitForRequest(tr);
                prop.setSpecularMapID(tr.getTexture().getId());
            }
            TextureLoaderRequest tex = new TextureLoaderRequest(current.getPath(), prop);
            GLRequestProcessor.sendRequest(tex);
            textureRequestByName.put(tex, name);
        }
    }

    private void loadAllSounds() throws Exception {
        File soundFolder = new File(Config.REPOSITORY_FOLDER + "/sounds/", DATA);
        if (soundFolder.exists()) {
            for (File currentFile : Objects.requireNonNull(soundFolder.listFiles())) {
                String json = JsonUtils.loadJson(currentFile.getAbsolutePath());
                if (json.isEmpty()) {
                    throw new Exception("a json a sound is empty... " + currentFile.getAbsolutePath());
                }
                SoundResources current = JsonUtils.gsonInstance().fromJson(json, SoundResources.class);
                if (current == null) {
                    throw new NullPointerException("The file cannot " + currentFile.getName() + " be read");
                }
                String name = current.getName();
                SoundRequest sr = new SoundRequest(current.getPath(), current.isAmbient());
                GLRequestProcessor.sendRequest(sr);
                soundRequestByName.put(sr, name);
            }
        }
    }

    private void loadAllModels() throws Exception {
        File modelFolder = new File(Config.REPOSITORY_FOLDER + "/models/", DATA);

        for (File currentFile : Objects.requireNonNull(modelFolder.listFiles())) {
            String json = JsonUtils.loadJson(currentFile.getAbsolutePath());
            if (json.isEmpty()) {
                throw new Exception("a json a model is empty... " + currentFile.getAbsolutePath());
            }
            ModelResources current = JsonUtils.gsonInstance().fromJson(json, ModelResources.class);
            if (current == null) {
                throw new NullPointerException("The file cannot " + currentFile.getName() + " be read");
            }
            String name = current.getName();
            if (current.canAnimated()) {
                ModelLoaderRequest model = ResourceLoader.loadTexturedAnimatedModel(current.getPath(), current.getTexture());
                GLRequestProcessor.sendRequest(model);
                modelRequestByName.put(model, name);
            } else {
                ModelLoaderRequest model = ResourceLoader.loadTexturedModel(current.getPath(), current.getTexture());
                GLRequestProcessor.sendRequest(model);
                modelRequestByName.put(model, name);
            }
        }
    }

    private void loadAllAnimations() throws Exception {
        File animationFolder = new File(Config.REPOSITORY_FOLDER + "/animations/", DATA);
        if (animationFolder.exists()) {
            for (File currentFile : Objects.requireNonNull(animationFolder.listFiles())) {
                String json = JsonUtils.loadJson(currentFile.getAbsolutePath());
                if (json.isEmpty()) {
                    throw new Exception("a json a animation is empty... " + currentFile.getAbsolutePath());
                }
                AnimationResources current = JsonUtils.gsonInstance().fromJson(json, AnimationResources.class);
                if (current == null) {
                    throw new NullPointerException("The file cannot " + currentFile.getName() + " be read");
                }
                current.loadAnimation();
                optimizeAnimation(current);
            }
        }
    }

    private void optimizeAnimation(AnimationResources current) {
        AnimatedModel model = animatedModelByName.get(current.getModelName());
        HashMap<String, Animation> batch = animationByName.get(model);
        if (batch != null) {
            batch.put(current.getAnimationName(), current.getAnimation());
        } else {
            HashMap<String, Animation> newBatch = new HashMap<>();
            newBatch.put(current.getAnimationName(), current.getAnimation());
            animationByName.put(model, newBatch);
        }

    }

    public static boolean isLoaded() {
        return isLoaded;
    }


    public HashMap<String, Texture> getTextureByName() {
        return textureByName;
    }

    public HashMap<String, SoundSource> getSoundByName() {
        return soundByName;
    }

    public static HashMap<String, Model> getModelByName() {
        return modelByName;
    }

    public HashMap<String, AnimatedModel> getAnimatedModelByName() {
        return animatedModelByName;
    }

    public HashMap<AnimatedModel, HashMap<String, Animation>> getAnimationByName() {
        return animationByName;
    }

    public static HashMap<Integer, String> getComponentsByID() {
        return componentsByID;
    }
}
