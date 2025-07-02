import java.io.File;
import java.util.List;
import org.json.*;

public class SwaggerHubDownloader {
    private static final String API_TOKEN = ConfigLoader.get("swaggerhub.api.token");
    private static final String ORG = ConfigLoader.get("swaggerhub.org");
    private static final String DOWNLOAD_DIR = ConfigLoader.get("swaggerhub.download.path");

    public static void main(String[] args) {
        try {
            SwaggerHubClient client = new SwaggerHubClient(API_TOKEN);
            String apiListUrl = "https://api.swaggerhub.com/apis/" + ORG;
            String apiListResponse = client.get(apiListUrl);

            JSONObject apiListJson = new JSONObject(apiListResponse);
            JSONArray apisArray = apiListJson.getJSONArray("apis");

            for (int i = 0; i < apisArray.length(); i++) {
                JSONObject api = apisArray.getJSONObject(i);
                String apiName = api.getString("name");
                System.out.println("\n🔍 Processing API: " + apiName);

                String versionsUrl = "https://api.swaggerhub.com/apis/" + ORG + "/" + apiName + "/versions";
                String versionListJson = client.get(versionsUrl);
                List<String> versions = SwaggerHubUtils.parseVersions(versionListJson);

                String apiFolder = DOWNLOAD_DIR + "/" + apiName;
                new File(apiFolder).mkdirs();

                for (String version : versions) {
                    String specUrl = "https://api.swaggerhub.com/apis/" + ORG + "/" + apiName + "/" + version + "?resolved=true&format=json";
                    String spec = client.get(specUrl);

                    String fileName = apiFolder + "/" + apiName + "-" + version + ".json";
                    SwaggerHubUtils.saveToFile(fileName, spec);
                    System.out.println("✅ " + apiName + " v" + version + " → " + fileName);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
