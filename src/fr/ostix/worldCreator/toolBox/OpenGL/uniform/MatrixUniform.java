package fr.ostix.worldCreator.toolBox.OpenGL.uniform;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class MatrixUniform extends Uniform {
    public MatrixUniform(String name) {
        super(name);
    }

    public void loadMatrixToUniform(Matrix4f m) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            m.get(fb);
            GL20.glUniformMatrix4fv(super.getLocation(), false, fb);
        }
    }
}
