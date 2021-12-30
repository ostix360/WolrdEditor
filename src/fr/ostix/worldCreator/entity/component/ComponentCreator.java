package fr.ostix.worldCreator.entity.component;


import fr.ostix.worldCreator.entity.*;

public interface ComponentCreator {

    Component loadComponent(String component, Entity entity);
}
