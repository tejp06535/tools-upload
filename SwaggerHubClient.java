import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class SwaggerHubClient {
    private final String apiToken;

    public SwaggerHubClient(String apiToken) {
        this.apiToken = apiToken;
    }

    public String get(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + apiToken);
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new IOException("GET failed: " + conn.getResponseCode() + " - " + urlStr);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    public void put(String urlStr, String jsonBody) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Authorization", "Bearer " + apiToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes());
        }

        if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
            throw new IOException("PUT failed: HTTP " + conn.getResponseCode() + " - " + urlStr);
        }
    }
}
