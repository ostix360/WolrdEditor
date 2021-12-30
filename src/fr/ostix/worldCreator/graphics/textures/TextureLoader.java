package fr.ostix.worldCreator.graphics.textures;

import fr.ostix.worldCreator.toolBox.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

import static fr.ostix.worldCreator.toolBox.Config.*;
import static org.lwjgl.opengl.GL11.*;

public class TextureLoader {

    private final int id;
    private final int width;
    private File file;

    public TextureLoader(int id, int width) {
        this.id = id;
        this.width = width;
    }

    public static TextureLoader loadTexture(String file, int mode, boolean isClampEdge) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(REPOSITORY_FOLDER + "/textures/" + file + ".png"));
        } catch (IOException e) {
            Logger.err("impossible de lire " + REPOSITORY_FOLDER + "/textures/" + file + ".png");
            e.printStackTrace();
        }
        assert image != null : "impossible de lire " + REPOSITORY_FOLDER + "/textures/" + file + ".png";
        int w = image.getWidth();
        int h = image.getHeight();

        int[] pixels = new int[w*h];
        image.getRGB(0,0,w,h,pixels,0,w);

        ByteBuffer buffer = BufferUtils.createByteBuffer(w*h*4);

        for (int y = 0;y<w;y++)
        {
            for (int x = 0;x<h;x++)
            {
                int i = pixels[x+y*w];
                buffer.put((byte) ((i >> 16) & 0xFF));
                buffer.put((byte) ((i >> 8) & 0xFF));
                buffer.put((byte) ((i ) & 0xFF));
                buffer.put((byte) ((i >> 24) & 0xFF));
            }
        }

        buffer.flip();


        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,id);
        glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,w,h,0,GL_RGBA,GL_UNSIGNED_BYTE,buffer);


        if (mode == TextureUtils.MIPMAP_MODE) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

        } else if (mode == TextureUtils.MIPMAP_ANISOTROPIC_MODE) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f);
            if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                GL11.glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
            } else {
                Logger.warn("Anisotropic filtering is not supported by your graphic card");
            }
        } else if (mode == TextureUtils.NEAREST_MODE) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        } else if (mode == TextureUtils.LINEAR_MODE){
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        }else{
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
        }
        if (isClampEdge) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        }

        glBindTexture(GL_TEXTURE_2D,0);

        return new TextureLoader(id,w);
    }

    public static TextureLoader loadTexture(InputStream file, int mode, boolean isClampEdge) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            Logger.err("impossible de lire " + file);
            e.printStackTrace();
        }
        if (image  == null){
            System.err.println("impossible de lire " + file);
            return null;
        }
        int w = image.getWidth();
        int h = image.getHeight();
        int[] pixels = new int[w*h];
        image.getRGB(0,0,w,h,pixels,0,w);

        ByteBuffer buffer = BufferUtils.createByteBuffer(w*h*4);

        for (int y = 0;y<w;y++)
        {
            for (int x = 0;x<h;x++)
            {
                int i = pixels[x+y*w];
                buffer.put((byte) ((i >> 16) & 0xFF));
                buffer.put((byte) ((i >> 8) & 0xFF));
                buffer.put((byte) ((i ) & 0xFF));
                buffer.put((byte) ((i >> 24) & 0xFF));
            }
        }

        buffer.flip();


        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,id);
        glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,w,h,0,GL_RGBA,GL_UNSIGNED_BYTE,buffer);


        if (mode == TextureUtils.MIPMAP_MODE) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

        } else if (mode == TextureUtils.MIPMAP_ANISOTROPIC_MODE) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f);
            if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                float amount = Math.min(0.6f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                GL11.glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
            } else {
                Logger.warn("Anisotropic filtering is not supported by your graphic card");
            }
        } else if (mode == TextureUtils.NEAREST_MODE) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        } else if (mode == TextureUtils.LINEAR_MODE){
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        }else{
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
        }
        if (isClampEdge) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        }

        glBindTexture(GL_TEXTURE_2D,0);

        return new TextureLoader(id,w);
    }
    public int getId() {
        return id;
    }

    public int getWidth() {
        return this.width;
    }

    public TextureLoader setFile(File file) {
        this.file = file;
        return this;
    }

    public File getFile() {
        return file;
    }
}
