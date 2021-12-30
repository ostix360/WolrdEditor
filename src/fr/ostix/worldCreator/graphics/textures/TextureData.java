package fr.ostix.worldCreator.graphics.textures;

import java.nio.ByteBuffer;

public class TextureData {
    private final int wight;
    private final int height;
    private final ByteBuffer buffer;

    public TextureData(int wight, int height, ByteBuffer buffer) {
        this.wight = wight;
        this.height = height;
        this.buffer = buffer;
    }

    public int getWight() {
        return wight;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
