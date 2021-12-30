package fr.ostix.worldCreator.entity.component.collision;


import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.*;

public class CollisionComponent extends Component {
    private final CollisionProperties properties;

    public CollisionComponent(Entity e, CollisionProperties properties) {
        super(ComponentType.COLLISION_COMPONENT, e);
        this.properties = properties;
    }

    @Override
    public void update() {

    }

    public CollisionProperties getProperties() {
        return properties;
    }
}
