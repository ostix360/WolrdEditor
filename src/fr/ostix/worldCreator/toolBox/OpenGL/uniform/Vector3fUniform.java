package fr.ostix.worldCreator.toolBox.OpenGL.uniform;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class Vector3fUniform extends Uniform {

    public Vector3fUniform(String name) {
        super(name);
    }

    public void loadVector3fToUniform(Vector3f value) {
        GL20.glUniform3f(super.getLocation(), value.x(), value.y(), value.z());
    }
}
