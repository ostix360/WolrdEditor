package fr.ostix.worldCreator.core.loader;


import fr.ostix.worldCreator.entity.animated.animation.animation.Animation;
import fr.ostix.worldCreator.entity.animated.animation.loaders.AnimationLoader;

public class LoadAnimation {


    private static Animation animation;

    public static Animation loadAnimatedModel(String animationFile) {

         return animation = AnimationLoader.loadAnimation(animationFile);

    }

    public static Animation getAnimation() {
        return animation;
    }
}
