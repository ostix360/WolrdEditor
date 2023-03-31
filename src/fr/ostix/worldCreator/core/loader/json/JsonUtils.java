package fr.ostix.worldCreator.core.loader.json;

import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonUtils {

    public static Gson gsonInstance() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        return gsonBuilder.create();
    }

    public static String loadJson(String jsonFile) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader =new BufferedReader(
                new InputStreamReader(Files.newInputStream(Paths.get(jsonFile))))){
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            return sb.toString();
        }catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}
