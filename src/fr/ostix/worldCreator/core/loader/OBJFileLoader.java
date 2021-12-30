package fr.ostix.worldCreator.core.loader;

import fr.ostix.worldCreator.core.Timer;
import fr.ostix.worldCreator.core.resourcesProcessor.*;
import fr.ostix.worldCreator.entity.Entity;
import fr.ostix.worldCreator.graphics.model.ModelData;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OBJFileLoader {
    public static void loadModel(String objFileName, Entity entity) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(objFileName));
        } catch(FileNotFoundException e) {
            System.err.println("OBJ file not found at: " + objFileName);
            return;
        }

        // input data lists
        List<Float> vertices = new ArrayList<Float>();
        List<Float> texCoords = new ArrayList<Float>();
        List<Float> normals = new ArrayList<Float>();

        // output data lists
        List<Float> v = new ArrayList<Float>();
        List<Float> t = new ArrayList<Float>();
        List<Float> n = new ArrayList<Float>();
        List<Integer> i = new ArrayList<Integer>();

        try {
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] parts = line.split(" ");
                if(parts[0].equalsIgnoreCase("v")) { // vertex position
                    vertices.add(Float.parseFloat(parts[1])); // x
                    vertices.add(Float.parseFloat(parts[2])); // y
                    vertices.add(Float.parseFloat(parts[3])); // z
                } else if(parts[0].equalsIgnoreCase("vt")) { // texture coordinate
                    texCoords.add(Float.parseFloat(parts[1])); // u
                    texCoords.add(1f - Float.parseFloat(parts[2])); // v
                } else if(parts[0].equalsIgnoreCase("vn")) { // normal vector
                    normals.add(Float.parseFloat(parts[1])); // x
                    normals.add(Float.parseFloat(parts[2])); // y
                    normals.add(Float.parseFloat(parts[3])); // z
                } else if(parts[0].equalsIgnoreCase("f")) {
                    int v0, v1, v2, v3, idx = v.size() / 3;
                    switch(parts.length) {
                        case 4: // triangle
                            v0 = appendVertex(parts[1].split("/"), vertices, texCoords, normals, v, t, n, idx);
                            v1 = appendVertex(parts[2].split("/"), vertices, texCoords, normals, v, t, n, idx + 1);
                            v2 = appendVertex(parts[3].split("/"), vertices, texCoords, normals, v, t, n, idx + 2);
                            i.add(v0);
                            i.add(v1);
                            i.add(v2);
                            break;
                        case 5: // quad
                            v0 = appendVertex(parts[1].split("/"), vertices, texCoords, normals, v, t, n, idx);
                            v1 = appendVertex(parts[2].split("/"), vertices, texCoords, normals, v, t, n, idx + 1);
                            v2 = appendVertex(parts[3].split("/"), vertices, texCoords, normals, v, t, n, idx + 2);
                            v3 = appendVertex(parts[4].split("/"), vertices, texCoords, normals, v, t, n, idx + 3);
                            // triangle 1
                            i.add(v0);
                            i.add(v1);
                            i.add(v2);
                            // triangle 2
                            i.add(v2);
                            i.add(v3);
                            i.add(v0);
                            break;
                        default: // unrecognized n-gon
                            System.out.println("Unrecognized vertex size: " + (parts.length - 1));
                            break;
                    }
                }
            }
        } catch(Exception e) {
            System.err.println("Failed to read OBJ model!");
            e.printStackTrace(System.err);
            return;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Failed to close reader!");
                e.printStackTrace();
            }
        }

        float[] vertexData = floatListToArray(v);
        float[] textureData = floatListToArray(t);
        float[] normalData = floatListToArray(n);
        int[] indexData = intListToArray(i);
//        GLRequest request = new ModelLoaderRequest(entity,new ModelData(vertexData,textureData,
//                indexData,normalData), false);
//        GLRequestProcessor.sendRequest(request);
//        Timer.waitForRequest(request);
    }


    private static int appendVertex(String[] vertex, List<Float> v, List<Float> t, List<Float> n, List<Float> ov, List<Float> ot, List<Float> on, int i) {
        int vi = Integer.parseInt(vertex[0]) - 1;
        int ti = Integer.parseInt(vertex[1]) - 1;
        int ni = Integer.parseInt(vertex[2]) - 1;
        // append vertex position
        ov.add(v.get(vi*3));
        ov.add(v.get(vi*3+1));
        ov.add(v.get(vi*3+2));
        // append texture coordinate
        ot.add(t.get(ti*2));
        ot.add(t.get(ti*2+1));
        // append normal vector
        on.add(n.get(ni*3));
        on.add(n.get(ni*3+1));
        on.add(n.get(ni*3+2));
        // return index
        return i;
    }

    private static float[] floatListToArray(List<Float> list) {
        float[] arr = new float[list.size()];
        for(int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    private static int[] intListToArray(List<Integer> list) {
        int[] arr = new int[list.size()];
        for(int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
    public static ModelData loadModel(InputStream objFileName) {
        InputStreamReader isr = null;
        isr = new InputStreamReader(objFileName);
        assert isr != null;
        BufferedReader reader = new BufferedReader(isr);
        String line;
        List<Vertex> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        try {
            while (true) {
                line = reader.readLine();
                if (line.startsWith("v ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    Vertex newVertex = new Vertex(vertices.size(), vertex);
                    vertices.add(newVertex);

                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split(" ");
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    break;
                }
            }
            while (line != null && line.startsWith("f ")) {
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                processVertex(vertex1, vertices, indices);
                processVertex(vertex2, vertices, indices);
                processVertex(vertex3, vertices, indices);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading the file");
        }
        removeUnusedVertices(vertices);
        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
                texturesArray, normalsArray);
        int[] indicesArray = convertIndicesListToArray(indices);
        return new ModelData(verticesArray,texturesArray,indicesArray,normalsArray);
    }

    private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
        int index = Integer.parseInt(vertex[0]) - 1;
        Vertex currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
        } else {
            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
                    vertices);
        }
    }

    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }

    private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
                                             List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
                                             float[] normalsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
        }
        return furthestPoint;
    }

    private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
                                                       int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
                        indices, vertices);
            } else {
                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
            }

        }
    }

    private static void removeUnusedVertices(List<Vertex> vertices) {
        for (Vertex vertex : vertices) {
            if (!vertex.isSet()) {
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }

}