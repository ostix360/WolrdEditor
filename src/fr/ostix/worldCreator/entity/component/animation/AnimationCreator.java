package fr.ostix.worldCreator.entity.component.animation;


import fr.ostix.worldCreator.core.resourcesLoader.*;
import fr.ostix.worldCreator.core.ressources.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.animated.animation.animatedModel.*;
import fr.ostix.worldCreator.entity.component.*;

public class AnimationCreator implements ComponentCreator {


    public Component loadComponent(String component, Entity entity) {
        AnimatedModel model;
        if (entity.getModel() instanceof AnimatedModel) {
            model = (AnimatedModel) entity.getModel();
        } else {
            new Exception("Animation component couldn't be created because your entity's model can't be animated");
            return null;
        }
        return new AnimationComponent(entity, ResourcePack.getAnimationByName().get(model));
    }
}
