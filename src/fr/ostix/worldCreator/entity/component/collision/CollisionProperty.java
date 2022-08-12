package fr.ostix.worldCreator.entity.component.collision;

public class CollisionProperty {
    private String controllerType = "RigidBody";

    public CollisionProperty() {
    }

    public String getControllerType() {
        return controllerType;
    }

    protected void setControllerType(String controllerType) {
        this.controllerType = controllerType;
    }
}
