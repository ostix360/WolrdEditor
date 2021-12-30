package fr.ostix.worldCreator.entity.camera;


import org.joml.*;

public interface ICamera {

    Matrix4f getViewMatrix();

    Matrix4f getProjectionMatrix();

    Matrix4f getProjectionViewMatrix();


    Vector3f getPosition();

}
