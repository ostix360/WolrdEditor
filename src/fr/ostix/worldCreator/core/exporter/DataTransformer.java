package fr.ostix.worldCreator.core.exporter;

import java.nio.ByteBuffer;

public class DataTransformer {

    public static ByteBuffer lineBuffer(String data) {
        data += "\n";
        byte[] bytes = data.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }
}
