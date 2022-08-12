package fr.ostix.worldCreator.entity.component.collision;

import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.*;

public class CollisionCreator implements ComponentCreator {


    @Override
    public Component loadComponent(String component, Entity entity) {
        String[] lines = component.split("\n");
        if (!lines[0].equalsIgnoreCase("Collision Component")) {
            return null;
        }
        CollisionProperty prop = new CollisionProperty();
        try {
            prop.setControllerType(lines[1]);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new CollisionComponent(entity, prop);
    }


}
