package fr.ostix.worldCreator.graphics.font.meshCreator;

/**
 * Simple data structure class holding information about a certain glyph in the
 * font texture atlas. All sizes are for a font-size of 1.
 *
 * @author Karl
 */
public class Character {

    private final int id;
    private final double xTextureCoord;
    private final double yTextureCoord;
    private final double xMaxTextureCoord;
    private final double yMaxTextureCoord;
    private final double xOffset;
    private final double yOffset;
    private final double sizeX;
    private final double sizeY;
    private final double xAdvance;

    protected Character(int id, double xTextureCoord, double yTextureCoord, double xTexSize, double yTexSize,
                        double xOffset, double yOffset, double sizeX, double sizeY, double xAdvance) {
        this.id = id;
        this.xTextureCoord = xTextureCoord;
        this.yTextureCoord = yTextureCoord;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.xMaxTextureCoord = xTexSize + xTextureCoord;
        this.yMaxTextureCoord = yTexSize + yTextureCoord;
        this.xAdvance = xAdvance;
    }

    protected int getId() {
        return id;
    }

    protected double getxTextureCoord() {
        return xTextureCoord;
    }

    protected double getyTextureCoord() {
        return yTextureCoord;
    }

    protected double getXMaxTextureCoord() {
        return xMaxTextureCoord;
    }

    protected double getYMaxTextureCoord() {
        return yMaxTextureCoord;
    }

    protected double getxOffset() {
        return xOffset;
    }

    protected double getyOffset() {
        return yOffset;
    }

    protected double getSizeX() {
        return sizeX;
    }

    protected double getSizeY() {
        return sizeY;
    }

    protected double getxAdvance() {
        return xAdvance;
    }

}
