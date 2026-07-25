package breach;

import java.io.IOException;
import java.util.List;

/**
 * Orchestrates password breach detection using k-anonymity.
 */
public class BreachChecker {

    private final HibpClient client;

    public BreachChecker() {
        this.client = new HibpClient();
    }

    /**
     * Checks if a password has been breached using Have I Been Pwned.
     * Guarantees k-anonymity: only the first 5 characters of SHA-1 hash are transmitted.
     */
    public BreachResult checkPassword(String password) {
        if (password == null || password.isEmpty()) {
            return new BreachResult(false, 0, "NONE", "Password is empty.");
        }

        // 1. Generate SHA-1 Hash
        String sha1Hash = SHA1Hasher.hash(password);
        if (sha1Hash.length() != 40) {
            return new BreachResult(false, 0, "NONE", "Failed to compute SHA-1 hash.");
        }

        // 2. Splicing: Prefix (first 5 chars) and Suffix (remaining 35 chars)
        String prefix = sha1Hash.substring(0, 5);
        String suffix = sha1Hash.substring(5);

        try {
            // 3. Request suffix list
            List<String> suffixLines = client.getHashSuffixes(prefix);

            // 4. Compare locally
            for (String line : suffixLines) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String responseSuffix = parts[0].trim();
                    if (responseSuffix.equalsIgnoreCase(suffix)) {
                        int breachCount = Integer.parseInt(parts[1].trim());
                        return new BreachResult(
                                true,
                                breachCount,
                                "CRITICAL",
                                "WARNING: This password was found in a public data breach! Exposes you to credential stuffing. Change immediately!"
                        );
                    }
                }
            }

            // If not found in suffix list
            return new BreachResult(
                    false,
                    0,
                    "NONE",
                    "No breaches detected. Your password is not known to be compromised."
            );

        } catch (IOException | InterruptedException e) {
            // Gracefully handle network failures / offline mode
            return new BreachResult(
                    false,
                    0,
                    "UNKNOWN",
                    "Breach check skipped: Have I Been Pwned API is offline or unreachable."
            );
        } catch (NumberFormatException e) {
            // Fallback for malformed counts
            return new BreachResult(
                    false,
                    0,
                    "UNKNOWN",
                    "Breach check error: Malformed server response."
            );
        }
    }
}
