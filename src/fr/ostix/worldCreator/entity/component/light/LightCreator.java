package fr.ostix.worldCreator.entity.component.light;


import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.*;
import fr.ostix.worldCreator.toolBox.*;
import org.joml.*;


public class LightCreator implements ComponentCreator {

    @Override
    public Component loadComponent(String component, Entity entity) {
        String[] lines = component.split("\n");
        Vector3f pos;
        Color color;
        Vector3f att;
        float power;
        String[] values = lines[0].split(";");
        pos = new Vector3f(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
        values = lines[1].split(";");
        color = new Color(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
        values = lines[2].split(";");
        att = new Vector3f(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
        power = Float.parseFloat(lines[3]);
        pos.add(entity.getPosition());
        return new Light(pos, color, power, att, entity);
    }


}
