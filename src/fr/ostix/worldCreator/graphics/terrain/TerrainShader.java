package fr.ostix.worldCreator.graphics.terrain;



import fr.ostix.worldCreator.entity.camera.*;
import fr.ostix.worldCreator.entity.component.light.*;
import fr.ostix.worldCreator.main.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.toolBox.OpenGL.shader.*;
import fr.ostix.worldCreator.toolBox.OpenGL.uniform.*;
import fr.ostix.worldCreator.world.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;


public class TerrainShader extends ShaderProgram {

    private final int MAX_LIGHTS = Main.MAX_LIGHTS;

    private final MatrixUniform transformationMatrix = new MatrixUniform("transformationMatrix");
    private final MatrixUniform projectionMatrix = new MatrixUniform("projectionMatrix");
    private final MatrixUniform viewMatrix = new MatrixUniform("viewMatrix");
    private final Vector3fUniformArray lightPos = new Vector3fUniformArray("lightPos", MAX_LIGHTS);
    private final Vector3fUniformArray lightColor = new Vector3fUniformArray("lightColor", MAX_LIGHTS);
    private final Vector3fUniformArray lightAttenuation = new Vector3fUniformArray("attenuation", MAX_LIGHTS);
    private final FloatUniformArray lightPower = new FloatUniformArray("lightPower", MAX_LIGHTS);
    private final FloatUniform shine = new FloatUniform("shine");
    private final FloatUniform reflectivity = new FloatUniform("reflectivity");
    private final Vector3fUniform skyColour = new Vector3fUniform("skyColor");
    private final IntUniform backgroundTexture = new IntUniform("backgroundTexture");
    private final IntUniform rTexture = new IntUniform("rTexture");
    private final IntUniform gTexture = new IntUniform("gTexture");
    private final IntUniform bTexture = new IntUniform("bTexture");
    private final IntUniform blendMap = new IntUniform("blendMap");
    private final Vector4fUniform plane = new Vector4fUniform("plane");
    private final MatrixUniform toShadowMapSpace = new MatrixUniform("toShadowMapSpace");
    private final IntUniform shadowMap = new IntUniform("shadowMap");
    private final Vector3fUniform filter = new Vector3fUniform("filters");

    public TerrainShader() {
        super("terrainShader");
        super.storeAllUniformsLocations(transformationMatrix, projectionMatrix, viewMatrix,
                reflectivity, shine, backgroundTexture, rTexture, gTexture, bTexture, blendMap, skyColour,
                lightPos, lightColor, lightAttenuation, lightPower,filter);
    }


    @Override
    protected void bindAllAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }


    public void connectTerrainUnits() {
        backgroundTexture.loadIntToUniform(0);
        rTexture.loadIntToUniform(1);
        gTexture.loadIntToUniform(2);
        bTexture.loadIntToUniform(3);
        blendMap.loadIntToUniform(4);
        shadowMap.loadIntToUniform(5);
    }


    public void loadShaderMapSpace(Matrix4f matrix) {
        toShadowMapSpace.loadMatrixToUniform(matrix);
    }

    public void loadClipPlane(Vector4f value) {
        plane.loadVec4fToUniform(value);
    }

    public void canLoadFilter(boolean can){
        if (can){
            filter.loadVector3fToUniform(World.getFilter());
        }else{
            filter.loadVector3fToUniform(new Vector3f(0,0,0));
        }
    }

    public void loadSkyColour(Color colour) {
        skyColour.loadVector3fToUniform(colour.getVec3f());
    }

    public void loadSpecular(float reflectivity, float shineDamper) {
        this.reflectivity.loadFloatToUniform(reflectivity);
        this.shine.loadFloatToUniform(shineDamper);
    }

    public void loadLight(List<Light> lights) {
        Vector3f[] pos = new Vector3f[MAX_LIGHTS];
        Vector3f[] color = new Vector3f[MAX_LIGHTS];
        Vector3f[] attenuation = new Vector3f[MAX_LIGHTS];
        float[] power = new float[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < lights.size()) {
                Light light = lights.get(i);
                pos[i] = light.getPosition();
                color[i] = light.getColourVec3f();
                attenuation[i] = light.getAttenuation();
                power[i] = light.getPower();
            } else {
                pos[i] = new Vector3f(0, 0, 0);
                color[i] = new Vector3f(0, 0, 0);
                attenuation[i] = new Vector3f(1, 0, 0);
                power[i] = 0F;
            }
        }
        lightPos.loadVector3fToUniform(pos);
        lightColor.loadVector3fToUniform(color);
        lightAttenuation.loadVector3fToUniform(attenuation);
        lightPower.loadFloatToUniform(power);

    }
    // Projection Transformation View Matrix

    public void loadTransformationMatrix(Matrix4f matrix) {
        transformationMatrix.loadMatrixToUniform(matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        projectionMatrix.loadMatrixToUniform(matrix);
    }

    public void loadViewMatrix(Camera cam) {
        viewMatrix.loadMatrixToUniform(Maths.createViewMatrix(cam));
    }


    @Override
    public String toString() {
        return "TerrainShader{}";
    }
}
