package fr.ostix.worldCreator.toolBox.OpenGL.uniform;

import org.lwjgl.opengl.GL20;

public class IntUniform extends Uniform {
    public IntUniform(String name) {
        super(name);
    }

    public void loadIntToUniform(int value) {
        GL20.glUniform1i(super.getLocation(), value);
    }
}
