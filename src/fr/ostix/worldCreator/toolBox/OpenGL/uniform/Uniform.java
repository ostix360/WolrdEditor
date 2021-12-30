package fr.ostix.worldCreator.toolBox.OpenGL.uniform;

import fr.ostix.worldCreator.toolBox.Logger;
import org.lwjgl.opengl.GL20;

public class Uniform {
    private final String name;
    private int location;

    public Uniform(String name) {
        this.name = name;
    }

    public boolean storeUniform(int programID) {
        location = GL20.glGetUniformLocation(programID, name);
        if (location == -1) {
            Logger.err("No uniform variable called " + name + " found!");
            return false;
        }
        return true;
    }

    protected int getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
