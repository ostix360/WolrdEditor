package fr.ostix.worldCreator.graphics.particles;


public class ParticleTexture {

    private final int texture;
    private final int numberOfRows;
    private final boolean additive;

    public ParticleTexture(int texture, int numberOfRows, boolean additive) {
        this.additive = additive;
        this.texture = texture;
        this.numberOfRows = numberOfRows;
    }

    public boolean isAdditive() {
        return additive;
    }

    public int getTextureID() {
        return texture;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }
}
