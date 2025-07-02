import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.*;

public class SwaggerHubUtils {
    public static List<String> parseVersions(String json) {
        JSONObject obj = new JSONObject(json);
        JSONArray arr = obj.getJSONArray("versions");
        List<String> versions = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            versions.add(arr.getString(i));
        }
        return versions;
    }

    public static void saveToFile(String filename, String content) throws IOException {
        Files.write(Paths.get(filename), content.getBytes());
    }

    public static String loadFromFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }
}
