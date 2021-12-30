package fr.ostix.worldCreator.toolBox.OpenGL.uniform;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

public class Vector4fUniform extends Uniform {
    public Vector4fUniform(String name) {
        super(name);
    }

    public void loadVec4fToUniform(Vector4f value) {
        GL20.glUniform4f(super.getLocation(), value.x(), value.y(), value.z(), value.w());
    }
}
