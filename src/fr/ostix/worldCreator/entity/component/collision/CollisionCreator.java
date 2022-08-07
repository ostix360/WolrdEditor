package fr.ostix.worldCreator.entity.component.collision;

import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.*;

import java.util.*;

public class CollisionCreator implements ComponentCreator {


    @Override
    public Component loadComponent(String component, Entity entity) {
        String[] lines = component.split("\n");
        if (!lines[0].equalsIgnoreCase("Collision Component")) {
            return null;
        }
        return new CollisionComponent(entity);
    }


}
