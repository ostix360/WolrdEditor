package fr.ostix.worldCreator.entity.component.particle;


import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.*;
import fr.ostix.worldCreator.graphics.particles.*;
import org.joml.*;

public class ParticleComponent extends Component {

    private final ParticleSystem system;
    private Vector3f offset = new Vector3f();

    public ParticleComponent(ParticleSystem system, Entity e) {
        super(ComponentType.PARTICLE_COMPONENT,e);
        this.system = system;
    }

    @Override
    public void update() {
        system.update(e.getPosition(), e.getRotation(), e.getScale());
        ParticleTarget target = system.getTarget();
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }
}
