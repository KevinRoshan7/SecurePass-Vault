package analyzer;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculates mathematical entropy of a password.
 */
public class EntropyCalculator {

    /**
     * Calculates the pool-based search space entropy: E = L * log2(R).
     * R is the character pool size based on character types present.
     */
    public static double calculatePoolEntropy(String password) {
        if (password == null || password.isEmpty()) {
            return 0.0;
        }

        int length = password.length();
        int poolSize = 0;

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (int i = 0; i < length; i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }

        if (hasLower) poolSize += 26;
        if (hasUpper) poolSize += 26;
        if (hasDigit) poolSize += 10;
        if (hasSpecial) poolSize += 33; // Standard symbols count

        if (poolSize == 0) return 0.0;

        // E = L * log2(R) = L * (ln(R) / ln(2))
        return length * (Math.log(poolSize) / Math.log(2.0));
    }

    /**
     * Calculates the Shannon Entropy of the password string to determine character distribution randomness.
     * H = -Sum(p_i * log2(p_i))
     */
    public static double calculateShannonEntropy(String password) {
        if (password == null || password.isEmpty()) {
            return 0.0;
        }

        int length = password.length();
        Map<Character, Integer> frequencies = new HashMap<>();

        for (int i = 0; i < length; i++) {
            char c = password.charAt(i);
            frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
        }

        double shannonEntropy = 0.0;
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            double probability = (double) entry.getValue() / length;
            shannonEntropy -= probability * (Math.log(probability) / Math.log(2.0));
        }

        // Standardize as bits (maximum Shannon entropy for a string of length L is log2(L) if all characters are unique)
        return shannonEntropy;
    }
}
