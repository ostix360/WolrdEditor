package fr.ostix.worldCreator.entity.component.ai;

import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.*;
import org.joml.*;

import java.lang.Math;

public class AIComponent extends Component {

    private final Random r = new Random();
    private final AIProperties properties;
    private float time = 0;
    private float pos = 0;
    private float rotY = 0;

    public AIComponent(Entity e, AIProperties properties) {
        super(ComponentType.AI_COMPONENT, e);
        this.properties = properties;
    }


    @Override
    public void update() {
        time++;
        if (time % (r.nextInt((int) properties.getUpdate()) + 30) == 0) {
            if (r.nextInt((int) properties.getStaticTime()) == 0) {
                pos = 0;
                e.setMovement(Entity.MovementType.STATIC);
            } else {
                pos = generatePositiveValue(properties.getSpeed(), properties.getSpeedError());

                e.setMovement(Entity.MovementType.FORWARD);
            }
            if (r.nextInt((int) properties.getRotateProbabilities()) <= 1) {
                rotY = generateRotation(properties.getSpeedTurn(), properties.getSpeedTurnError());
            } else {
                rotY = 0;
            }

        }

        float dx = (float) (pos * Math.sin(Math.toRadians(e.getRotation().y())));
        float dz = (float) (pos * Math.cos(Math.toRadians(e.getRotation().y())));
//        e.getTorque().set(new Vector3(0, rotY, 0));
//        e.increaseRotation(new Vector3f(0,rotY,0));
//        e.getForceToCenter().add(new Vector3(dx, 0, dz));
    }

    private float generateRotation(float average, float errorMargin) {
        float offset = (r.nextFloat() - 0.5f) * 2f * errorMargin;
        return average + offset;
    }


    private float generatePositiveValue(float average, float errorMargin) {
        float offset = (r.nextFloat()) * errorMargin;
        return average + offset;
    }
}
