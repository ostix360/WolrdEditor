package fr.ostix.worldCreator.toolBox.OpenGL.uniform;

import org.joml.*;

public class Vector3fUniformArray extends Uniform {

    private final Vector3fUniform[] vector3fUniforms;

    public Vector3fUniformArray(String name, int size) {
        super(name);
        vector3fUniforms = new Vector3fUniform[size];
        for (int i = 0; i < size; i++) {
            vector3fUniforms[i] = new Vector3fUniform(name + "[" + i + "]");
        }
    }

    @Override
    public boolean storeUniform(int programID) {
        for (Vector3fUniform vector3fUniform : vector3fUniforms) {
            return vector3fUniform.storeUniform(programID);
        }
        return false;
    }

    public void loadVector3fToUniform(Vector3f[] values) {
        for (int i = 0; i < values.length; i++) {
            vector3fUniforms[i].loadVector3fToUniform(values[i]);
        }
    }
}
