package fr.ostix.worldCreator.entity.component.light;


import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.*;
import fr.ostix.worldCreator.main.*;
import fr.ostix.worldCreator.toolBox.*;
import org.joml.*;

public class Light extends Component {
    private Vector3f position;
    private final Color colour;
    private final Vector3f attenuation;
    private final float power;


    public Light(Vector3f position, Color colour, float power, Entity e) {
        this(position, colour, power, new Vector3f(1, 0, 0), e);
    }

    public Light(Vector3f position, Color colour, Entity e) {
        this(position, colour, 1.0f, e);
    }

    public Light(Vector3f position, Color colour, float power, Vector3f attenuation, Entity e) {
        super(ComponentType.LIGHT_COMPONENT, e);
        this.position = position;
        this.colour = colour;
        this.attenuation = attenuation;
        this.power = power;
        //Main.addLight(this);
    }

    public float getPower() {
        return power;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColourVec3f() {
        return colour.getVec3f();
    }

    @Override
    public void update() {

    }
}
