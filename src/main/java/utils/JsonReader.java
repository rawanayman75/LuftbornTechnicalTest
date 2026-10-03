package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

public class JsonReader {
    private static JsonNode data;

    static {
        try (InputStream is = JsonReader.class.getClassLoader()
                .getResourceAsStream("testdata.json")) {
            data = new ObjectMapper().readTree(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load testdata.json", e);
        }
    }

    public static String get(String key) {
        return data.get(key).asText();
    }
}
