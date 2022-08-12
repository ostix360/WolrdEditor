package fr.ostix.worldCreator.entity.component.collision;


import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.control.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.*;

public class CollisionComponent extends Component {

    private PhysicsControl physic;

    private final CollisionProperty prop;

    public CollisionComponent(Entity e, CollisionProperty prop) {
        super(ComponentType.COLLISION_COMPONENT, e);
        this.prop = prop;
        if (prop.getControllerType().equals("Character")) {
            physic = new CharacterControl(new MeshCollisionShape(e.getModel().getMeshModel()),4f);
        }else{
            physic = new RigidBodyControl(0f);
        }

    }

    @Override
    public void update() {
        physic.update(1/60f);
    }

    public PhysicsControl getPhysic() {
        return physic;
    }

    public CollisionProperty getProp() {
        return prop;
    }
}
