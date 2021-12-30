package fr.ostix.worldCreator.entity.component.collision;


import com.flowpowered.react.collision.shape.*;
import fr.ostix.worldCreator.entity.*;

import java.util.*;

public class CollisionProperties {
    private boolean canMove = false;
    private boolean useSpecialBoundingBox;
    private List<BoundingModel> boundingModels = new ArrayList<>();

    public CollisionProperties() {
    }

    public CollisionProperties(CollisionProperties properties) {
        this.canMove = properties.canMove;
        for (BoundingModel b : properties.getBoundingModels()){
            if (b instanceof CollisionShape){
                this.boundingModels.add(((CollisionShape)b).clone().setTransform(b.getTransform()));
            }else{
                this.boundingModels.add(b.clone());
            }

        }
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public List<BoundingModel> getBoundingModels() {
        return boundingModels;
    }

    public boolean canMove() {
        return canMove;
    }

    public boolean useSpecialBoundingBox() {
        return useSpecialBoundingBox;
    }

    public void setBoundingModels(List<BoundingModel> boundingModels) {
        this.boundingModels = boundingModels;
    }
}
