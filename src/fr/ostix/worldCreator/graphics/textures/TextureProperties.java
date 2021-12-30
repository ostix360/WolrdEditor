package fr.ostix.worldCreator.graphics.textures;

import com.google.gson.annotations.SerializedName;

public class TextureProperties {

    private final boolean additive;
    private final boolean affectedByLighting;

    private int specularMapID;
    @SerializedName("specularMap")
    private String specularMapName;

    private int normalMapID;
    @SerializedName("normalMap")
    private String normalMapName;

    private float shineDamper = 1;
    private float reflectivity = 1;

    private final int numbersOfRows;

    private final boolean isTransparency;
    private final boolean useFakeLighting;
    private final boolean isInverseNormal;

    public TextureProperties(boolean additive, boolean affectedByLighting, int normalMapID, int specularMap, float shineDamper, float reflectivity, int numbersOfRows, boolean isTransparency, boolean useFakeLighting, boolean isInverseNormal) {
        this.additive = additive;
        this.affectedByLighting = affectedByLighting;
        this.normalMapID = normalMapID;
        this.specularMapID = specularMap;
        this.shineDamper = shineDamper;
        this.reflectivity = reflectivity;
        this.numbersOfRows = numbersOfRows;
        this.isTransparency = isTransparency;
        this.useFakeLighting = useFakeLighting;
        this.isInverseNormal = isInverseNormal;
    }

    public boolean isAdditive() {
        return additive;
    }

    public boolean isAffectedByLighting() {
        return affectedByLighting;
    }

    public String getSpecularMapName() {
        return specularMapName;
    }

    public String getNormalMapName() {
        return normalMapName;
    }

    public void setSpecularMapID(int specularMapID) {
        this.specularMapID = specularMapID;
    }

    public void setNormalMapID(int normalMapID) {
        this.normalMapID = normalMapID;
    }

    public int getNormalMapID() {
        return normalMapID;
    }

    public int getSpecularMapID() {
        return specularMapID;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public int getNumbersOfRows() {
        if (numbersOfRows == 0) {
            return 1;
        }
        return numbersOfRows;
    }

    public boolean isTransparency() {
        return isTransparency;
    }

    public boolean useFakeLighting() {
        return useFakeLighting;
    }

    public boolean isInverseNormal() {
        return isInverseNormal;
    }

    public boolean hasSpecularMap() {
        return specularMapID != 0;
    }
}
