package fr.ostix.worldCreator.entity.component;

import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.collision.*;

public class LoadComponents {

    public static void loadComponents(final String components, final Entity entity) {
        String[] lines = components.split("\n");
        for (int i = 0; i < lines.length; i++) {
            ComponentType type = getComponentType(lines[i]);
            if (type != null) {
                if (type.equals(ComponentType.COLLISION_COMPONENT)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Collision Component").append("\n");
                    for (int i1 = i + 1; i1 < lines.length; i1++) {
                        ComponentType tType = getComponentType(lines[i1]);
                        if (tType == null) {
                            sb.append(lines[i1]).append("\n");
                        } else {
                            break;
                        }
                    }
                    entity.setCollision((CollisionComponent) type.loadComponent(entity, sb.toString()));
                } else {
                    i++;
                    StringBuilder sb = new StringBuilder();
                    for (int l = 0; l < type.getNbLine(); l++) {
                        sb.append(lines[i++]).append("\n");
                    }
                    entity.addComponent(type.loadComponent(entity, sb.toString()));
                }
            }
        }

    }

    private static ComponentType getComponentType(String line) {
        for (ComponentType type : ComponentType.values()) {
            if (line.equals(type.toString())) {
                return type;
            }
        }
        return null;
    }
}
