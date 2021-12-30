package fr.ostix.worldCreator.toolBox.OpenGL;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VBO {

    private final int id;

    private VBO(int id) {
        this.id = id;
    }

    public static VBO createVBO() {
        int id = glGenBuffers();
        return new VBO(id);
    }

    public static VBO createEmptyVBO(int count) {
        VBO vbo = createVBO();
        glBindBuffer(GL_ARRAY_BUFFER, vbo.id);
        glBufferData(GL_ARRAY_BUFFER, (long) count * Float.BYTES, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public void storeDataInAttributeList(int attrib, int dataSize, float[] data) {
        glBindBuffer(GL_ARRAY_BUFFER, this.id); //Activation de l'addresse memoir
        FloatBuffer buffer = createFloatBuffer(data);   //creation d'une memoir tampon (Buffer) du tableau a ajouter dans notre VAO
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);  //Definition des données dans une memoir tampon (Buffer)
        GL20.glVertexAttribPointer(attrib, dataSize, GL11.GL_FLOAT, false, 0, 0);     //Definition de l'index,nombre de donné a lire dans le tableau par arrete,type de variable,sont des vecteur normalizer ou pas dans la memoir tampon
        glBindBuffer(GL_ARRAY_BUFFER, 0);       //Desactivation du VBO actife
    }

    public void storeIntDataInAttributeList(int attrib, int dataSize, int[] data) {
        glBindBuffer(GL_ARRAY_BUFFER, this.id); //Activation de l'addresse memoir
        IntBuffer buffer = createIntBuffer(data);   //creation d'une memoir tampon (Buffer) du tableau a ajouter dans notre VAO
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);  //Definition des données dans une memoir tampon (Buffer)
        GL30.glVertexAttribIPointer(attrib, dataSize, GL11.GL_INT, 0, 0);     //Definition de l'index,nombre de donné a lire dans le tableau par arrete,type de variable,sont des vecteur normalizer ou pas dans la memoir tampon
        glBindBuffer(GL_ARRAY_BUFFER, 0);       //Desactivation du VBO actife
    }

    public void addInstance(int attrib, int dataSize,int instanceDataLength, int offset){
        glBindBuffer(GL_ARRAY_BUFFER, this.id);
        GL20.glVertexAttribPointer(attrib, dataSize, GL_FLOAT, false, instanceDataLength * Float.BYTES, (long) offset * Float.BYTES);
        GL33.glVertexAttribDivisor(attrib, 1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void storeIndicesDataInAttributeList(int[] indices) {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.id); //Activation de l'addresse memoir
        IntBuffer buffer = createIntBuffer(indices);   //creation d'une memoir tampon (Buffer) du tableau a ajouter dans notre VAO
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);  //Definition des données dans une memoir tampon (Buffer)
    }

    private FloatBuffer createFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);   //Creation d'une memoir tampon avec la longeur du tableau
        buffer.put(data);         //On met les données dans la memoir tampon
        buffer.flip();              //Permet de lire la memoir tampon
        return buffer;
    }

    public void updateVBO(FloatBuffer buffer){
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, (long) buffer.capacity() * Float.BYTES, GL_STREAM_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }


    private IntBuffer createIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);   //Creation d'une memoir tampon avec la longeur du tableau
        buffer.put(data);         //On met les données dans la memoir tampon
        buffer.flip();              //Permet de lire la memoir tampon
        return buffer;
    }


    public void delete() {
        GL15.glDeleteBuffers(id);
    }



}
