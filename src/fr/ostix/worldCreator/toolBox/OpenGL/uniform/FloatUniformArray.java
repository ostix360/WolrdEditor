package fr.ostix.worldCreator.toolBox.OpenGL.uniform;

public class FloatUniformArray extends Uniform {
    private final FloatUniform[] floatUniforms;

    public FloatUniformArray(String name, int size) {
        super(name);
        floatUniforms = new FloatUniform[size];
        for (int i = 0; i < size; i++) {
            floatUniforms[i] = new FloatUniform(name + "[" + i + "]");
        }
    }

    @Override
    public boolean storeUniform(int programID) {
        for (FloatUniform vector3fUniform : floatUniforms) {
            return vector3fUniform.storeUniform(programID);
        }
        return false;
    }

    public void loadFloatToUniform(float[] values) {
        for (int i = 0; i < values.length; i++) {
            floatUniforms[i].loadFloatToUniform(values[i]);
        }
    }
}
