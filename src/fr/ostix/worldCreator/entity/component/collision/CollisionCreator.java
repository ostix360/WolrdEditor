package fr.ostix.worldCreator.entity.component.collision;

import com.flowpowered.react.collision.shape.*;
import com.flowpowered.react.collision.shape.CollisionShape.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.*;

import java.util.*;

public class CollisionCreator implements ComponentCreator {


    @Override
    public Component loadComponent(String component, Entity entity) {
        CollisionProperties prop = new CollisionProperties();
        List<BoundingModel> bounds = new ArrayList<>();
        String[] lines = component.split("\n");
        if (!lines[0].equalsIgnoreCase("Collision Component")) {
            return null;
        }
        for (int i = 1; i < lines.length - 1; i++) {
            String line = lines[i];
            CollisionShape.CollisionShapeType type = isType(line);
            if (type != null) {
                BoundingModel model;
                switch (type) {
                    case BOX:
                        model = BoxShape.load(lines[++i]);
                        break;
                    case CAPSULE:
                        model = CapsuleShape.load(lines[++i]);
                        break;
                    case CONE:
                        model = ConeShape.load(lines[++i]);
                        break;
                    case CYLINDER:
                        model = CylinderShape.load(lines[++i]);
                        break;
                    default:
                        model = SphereShape.load(lines[++i]);
                }
                Transform t = Transform.load(lines[++i]);
                model.setTransform(t);
                bounds.add(model);
            } else {
                String sb = line + "\n" +
                        lines[i++];
                bounds.add(BoundingModel.load(sb));
            }
        }
        prop.setCanMove(Boolean.parseBoolean(lines[lines.length - 1]));
        prop.setBoundingModels(bounds);

        return new CollisionComponent(entity, prop);
    }

    private CollisionShape.CollisionShapeType isType(String line) {
        for (CollisionShapeType type : CollisionShapeType.values()) {
            if (line.equalsIgnoreCase(type.toString())) {
                return type;
            }
        }
        return null;
    }
}
