package fr.ostix.worldCreator.graphics.particles;

public class ParticleTargetProperties {
    private float xOffset;
    private float yOffset;
    private float zOffset;
    private float pullFactor;
    private float cutOff;

    public ParticleTargetProperties(float xOffset, float yOffset, float zOffset, float pullFactor, float cutOff) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.pullFactor = pullFactor;
        this.cutOff = cutOff;
    }

    public float getxOffset() {
        return xOffset;
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public float getzOffset() {
        return zOffset;
    }

    public void setzOffset(float zOffset) {
        this.zOffset = zOffset;
    }

    public float getPullFactor() {
        return pullFactor;
    }

    public void setPullFactor(float pullFactor) {
        this.pullFactor = pullFactor;
    }

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }
}
