package similarity;

import java.util.ArrayList;
import java.util.List;
import storage.Credential;
import utils.Constants;

/**
 * Detects password similarity and reuse patterns across the vault using Levenshtein Distance.
 */
public class PasswordSimilarityDetector {

    /**
     * Scans all stored credentials for similarity and groups similar entries.
     */
    public List<SimilarityResult> detectSimilarities(List<Credential> credentials) {
        List<SimilarityResult> results = new ArrayList<>();
        if (credentials == null || credentials.size() < 2) {
            return results;
        }

        // Pairwise comparison of passwords
        for (int i = 0; i < credentials.size(); i++) {
            Credential c1 = credentials.get(i);
            for (int j = i + 1; j < credentials.size(); j++) {
                Credential c2 = credentials.get(j);

                double similarityPct = calculateSimilarityPercentage(c1.getPassword(), c2.getPassword());

                if (similarityPct >= Constants.SIMILARITY_THRESHOLD_PCT) {
                    int distance = calculateLevenshteinDistance(c1.getPassword(), c2.getPassword());
                    String riskLevel;
                    String recommendation;

                    if (similarityPct == 100.0) {
                        riskLevel = "CRITICAL";
                        recommendation = String.format(
                                "CRITICAL REUSE: Both records (\"%s\" and \"%s\") use identical passwords. " +
                                "If one is compromised, both are compromised! Generate a unique password for one.",
                                c1.getWebsite(), c2.getWebsite()
                        );
                    } else if (similarityPct >= 85.0) {
                        riskLevel = "HIGH";
                        recommendation = String.format(
                                "HIGH SIMILARITY (%.1f%%): Passwords for \"%s\" and \"%s\" are highly similar. " +
                                "Change one to prevent pattern-guessing attacks.",
                                similarityPct, c1.getWebsite(), c2.getWebsite()
                        );
                    } else {
                        riskLevel = "MEDIUM";
                        recommendation = String.format(
                                "MEDIUM SIMILARITY (%.1f%%): Passwords for \"%s\" and \"%s\" show variations of a shared root. " +
                                "Consider replacing with a generated password.",
                                similarityPct, c1.getWebsite(), c2.getWebsite()
                        );
                    }

                    results.add(new SimilarityResult(c1, c2, distance, similarityPct, riskLevel, recommendation));
                }
            }
        }

        return results;
    }

    /**
     * Calculates the percentage similarity of two strings.
     */
    public static double calculateSimilarityPercentage(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        if (s1.equals(s2)) return 100.0;

        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) return 100.0;

        int distance = calculateLevenshteinDistance(s1, s2);
        return (1.0 - (double) distance / maxLength) * 100.0;
    }

    /**
     * Standard dynamic programming implementation of Levenshtein Distance.
     */
    public static int calculateLevenshteinDistance(String s1, String s2) {
        if (s1 == null) return s2 == null ? 0 : s2.length();
        if (s2 == null) return s1.length();

        int len1 = s1.length();
        int len2 = s2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), // deletion / insertion
                        dp[i - 1][j - 1] + cost                     // substitution
                );
            }
        }

        return dp[len1][len2];
    }
}
