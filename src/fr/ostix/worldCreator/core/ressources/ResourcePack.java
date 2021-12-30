package fr.ostix.worldCreator.core.ressources;

import fr.ostix.worldCreator.audio.*;
import fr.ostix.worldCreator.entity.animated.animation.animatedModel.*;
import fr.ostix.worldCreator.entity.animated.animation.animation.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.graphics.textures.*;

import java.util.*;

public class ResourcePack {
    private static HashMap<String, Texture> textures;
    private final HashMap<String, SoundSource> sounds;
    private final HashMap<String, Model> models;
    private static HashMap<AnimatedModel, HashMap<String, Animation>> animations;
    private final HashMap<String, AnimatedModel> animatedModels;
    private final HashMap<Integer, String> components;

    public ResourcePack(HashMap<String, Texture> textures, HashMap<String, SoundSource> sounds,
                        HashMap<String, Model> models, HashMap<String, AnimatedModel> animatedModelByName,
                        HashMap<AnimatedModel, HashMap<String, Animation>> animations, HashMap<Integer, String> components) {
        ResourcePack.textures = textures;
        this.sounds = sounds;
        this.models = models;
        this.animatedModels = animatedModelByName;
        ResourcePack.animations = animations;
        this.components = components;
    }

    public static HashMap<String, Texture> getTextureByName() {
        return textures;
    }

    public HashMap<String, SoundSource> getSoundByName() {
        return sounds;
    }

    public HashMap<String, Model> getModelByName() {
        return models;
    }

    public static HashMap<AnimatedModel, HashMap<String, Animation>> getAnimationByName() {
        return animations;
    }

    public HashMap<String, AnimatedModel> getAnimatedModelByName() {
        return animatedModels;
    }


    public HashMap<Integer, String> getComponents() {
        return components;
    }
}
