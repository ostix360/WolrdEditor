package fr.ostix.worldCreator.entity.component.animation;


import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.animated.animation.animatedModel.*;
import fr.ostix.worldCreator.entity.animated.animation.animation.*;
import fr.ostix.worldCreator.entity.component.*;
import fr.ostix.worldCreator.toolBox.*;
import org.joml.Random;

import java.util.*;

public class AnimationComponent extends Component {

    private final HashMap<String, Animation> animations;

    public AnimationComponent(Entity e, HashMap<String, Animation> animations) {
        super(ComponentType.ANIMATED_COMPONENT, e);
        if (!(e.getModel() instanceof AnimatedModel)) {
            System.err.println("Your Model is not an AnimatedModel");
            System.exit(-1);
        }
        this.animations = animations;
    }

    @Override
    public void update() {
        AnimatedModel model = (AnimatedModel) e.getModel();
        Animation a = animations.get(e.getMovement().getId());
        if (a == null) {
            if (new Random().nextInt(4) == 0)
                Logger.warn("The animation, " + e.getMovement().getId() + " for the model " + " is not available");
        } else {
            if (model.getPriorityAnimation() == null) {
                model.doAnimation(a);
            } else if (!model.getPriorityAnimation().equals(a)) {
                model.doAnimation(a);
            }
        }
        if (model.getPriorityAnimation() != null) {
            model.update(1f / 60f);
        }
    }
}
