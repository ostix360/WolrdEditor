package fr.ostix.worldCreator.entity.component.ai;


import fr.ostix.worldCreator.core.loader.json.*;
import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.entity.component.*;
import fr.ostix.worldCreator.toolBox.*;

public class AICreator implements ComponentCreator {

    @Override
    public Component loadComponent(String component, Entity e) {
        AIComponent ai = null;
        try {
            AIProperties prop = JsonUtils.gsonInstance().fromJson(component, AIProperties.class);
            ai = new AIComponent(e, prop);
        } catch (Exception ex) {
            Logger.err("Failed to load AI Component");
            ex.printStackTrace();
        }
        return ai;
    }
}
