package breach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import utils.Constants;

/**
 * HTTP Client to interface with HIBP API using k-anonymity.
 */
public class HibpClient {

    private final HttpClient httpClient;

    public HibpClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(Constants.HTTP_TIMEOUT_SECONDS))
                .build();
    }

    /**
     * Queries the HIBP API range search for the given 5-character hash prefix.
     * Returns a list of matching suffix:count lines.
     */
    public List<String> getHashSuffixes(String prefix5) throws IOException, InterruptedException {
        if (prefix5 == null || prefix5.length() != 5) {
            throw new IllegalArgumentException("Prefix must be exactly 5 characters long");
        }

        String url = Constants.HIBP_API_URL + prefix5;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(Constants.HTTP_TIMEOUT_SECONDS))
                .header("User-Agent", "SecurePass Vault Console App - Java Academic Project")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                if (body == null || body.isBlank()) {
                    return Collections.emptyList();
                }
                // Split lines
                String[] lines = body.split("\\R");
                return List.of(lines);
            } else {
                throw new IOException("Server responded with HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            // Re-throw so callers know it's a connection failure
            throw e;
        }
    }
}
