package fr.ostix.worldCreator.toolBox;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.BufferUtils.createByteBuffer;

public class FileUtils {



    public static ByteBuffer loadByteBufferFromResources(String resource,int bufferSize) {
        ByteBuffer buffer = null;

        Path path = Paths.get("/sounds/" + resource + FileType.OGG);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (InputStream source = FileUtils.class.getResourceAsStream("/sounds/" + resource + ".ogg")) {
                assert source != null;
                try (ReadableByteChannel rbc = Channels.newChannel(source)) {
                    buffer = createByteBuffer(bufferSize);

                    while (true) {
                        int bytes = rbc.read(buffer);
                        if (bytes == -1) {
                            break;
                        }
                        if (buffer.remaining() == 0) {
                            buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(buffer == null){
            try {
                throw new Exception("AudioBuffer is null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        assert buffer != null;
        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}
