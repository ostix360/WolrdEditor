package fr.ostix.worldCreator.core.loader.json;

import com.google.gson.*;

import java.io.*;

public class JsonUtils {

    public static Gson gsonInstance() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        return gsonBuilder.create();
    }

    public static String loadJson(String jsonFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader;

        reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(jsonFile)));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }

        return sb.toString();
    }
}
