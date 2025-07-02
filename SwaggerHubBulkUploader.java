import java.io.File;

public class SwaggerHubBulkUploader {
    private static final String API_TOKEN = ConfigLoader.get("swaggerhub.api.token");
    private static final String ORG = ConfigLoader.get("swaggerhub.org");
    private static final String UPLOAD_DIR = ConfigLoader.get("swaggerhub.upload.path");

    public static void main(String[] args) {
        try {
            SwaggerHubClient client = new SwaggerHubClient(API_TOKEN);

            File baseDir = new File(UPLOAD_DIR);
            if (!baseDir.exists() || !baseDir.isDirectory()) {
                throw new RuntimeException("Upload folder not found: " + UPLOAD_DIR);
            }

            for (File apiFolder : baseDir.listFiles(File::isDirectory)) {
                String apiName = apiFolder.getName();

                for (File specFile : apiFolder.listFiles((dir, name) -> name.endsWith(".json"))) {
                    String fileName = specFile.getName();
                    String version = extractVersionFromFilename(fileName, apiName);
                    if (version == null) {
                        System.out.println("❌ Skipping invalid filename: " + fileName);
                        continue;
                    }

                    String specContent = SwaggerHubUtils.loadFromFile(specFile.getPath());
                    String uploadUrl = "https://api.swaggerhub.com/apis/" + ORG + "/" + apiName + "/" + version;

                    client.put(uploadUrl, specContent);
                    System.out.println("✅ Uploaded: " + apiName + " v" + version);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Upload failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String extractVersionFromFilename(String fileName, String apiName) {
        String prefix = apiName + "-";
        String suffix = ".json";

        if (fileName.startsWith(prefix) && fileName.endsWith(suffix)) {
            return fileName.substring(prefix.length(), fileName.length() - suffix.length());
        }
        return null;
    }
}
