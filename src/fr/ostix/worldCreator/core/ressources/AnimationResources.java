package fr.ostix.worldCreator.core.ressources;


import fr.ostix.worldCreator.core.loader.*;
import fr.ostix.worldCreator.entity.animated.animation.animation.*;
import fr.ostix.worldCreator.toolBox.*;

public class AnimationResources {
    private Animation animation;

    private final String modelName;
    private final String animationName;

    public AnimationResources(String modelName, String animationName) {
        this.modelName = modelName;
        this.animationName = animationName;
    }

    public void loadAnimation() {
        LoadAnimation.loadAnimatedModel(Config.REPOSITORY_FOLDER+"/animations/" +modelName + "/" + animationName+".dae");
        this.animation = LoadAnimation.getAnimation();
    }

    public Animation getAnimation() {
        return animation;
    }


    public String getModelName() {
        return modelName;
    }

    public String getAnimationName() {
        return animationName;
    }
}
