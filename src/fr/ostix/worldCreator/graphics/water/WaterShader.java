package fr.ostix.worldCreator.graphics.water;


import fr.ostix.worldCreator.toolBox.OpenGL.shader.*;
import fr.ostix.worldCreator.toolBox.OpenGL.uniform.*;

public class WaterShader extends ShaderProgram {


    public final MatrixUniform modelMatrix = new MatrixUniform("modelMatrix");
    public final MatrixUniform viewMatrix = new MatrixUniform("viewMatrix");
    public final MatrixUniform projectionMatrix = new MatrixUniform("projectionMatrix");

    public final FloatUniform moveFactor = new FloatUniform("moveFactor");
    public final Vector3fUniform cameraPosition = new Vector3fUniform("cameraPosition");
    public final Vector3fUniform lightDirection = new Vector3fUniform("lightDirection");

    private final IntUniform reflectionTexture = new IntUniform("reflectionTexture");
    private final IntUniform refractionTexture = new IntUniform("refractionTexture");
    private final IntUniform dudvMap = new IntUniform("dudvMap");
    private final IntUniform normalMap = new IntUniform("normalMap");
    private final IntUniform depthMap = new IntUniform("depthMap");

    public WaterShader() {
        super("water");
        super.storeAllUniformsLocations(modelMatrix, viewMatrix, projectionMatrix, moveFactor,
                cameraPosition, lightDirection, reflectionTexture, refractionTexture,
                dudvMap, normalMap, depthMap);
        connectTextureUnits();
    }

    private void connectTextureUnits() {
        super.bind();
        reflectionTexture.loadIntToUniform(0);
        refractionTexture.loadIntToUniform(1);
        dudvMap.loadIntToUniform(2);
        normalMap.loadIntToUniform(3);
        depthMap.loadIntToUniform(4);
        super.unBind();
    }

    @Override
    protected void bindAllAttributes() {
        super.bindAttribute(0, "in_position");
    }

    @Override
    public String toString() {
        return "WaterShader{}";
    }
}
