package generator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Structural composer for personalized memorable passwords using SecureRandom.
 */
public class PasswordComposer {

    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";
    private final SecureRandom random = new SecureRandom();

    /**
     * Composes a password based on desired length and preferred words.
     */
    public String compose(int length, List<String> preferredWords) {
        if (preferredWords == null || preferredWords.isEmpty()) {
            return generateRandomFallback(length);
        }

        // 1. Clone and Shuffle words list using SecureRandom
        List<String> words = new ArrayList<>(preferredWords);
        Collections.shuffle(words, random);

        // 2. Mutate capitalization
        for (int i = 0; i < words.size(); i++) {
            words.set(i, mutateWordCase(words.get(i)));
        }

        // 3. Assemble components
        StringBuilder sb = new StringBuilder();
        char separator = SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length()));
        int randomNumber = 10 + random.nextInt(90); // 2-digit number (10-99)

        // Alternate joining words and separators
        for (int i = 0; i < words.size(); i++) {
            sb.append(words.get(i));
            if (i < words.size() - 1) {
                sb.append(separator);
            }
        }

        // Insert numbers and extra symbols
        if (random.nextBoolean()) {
            sb.append(separator).append(randomNumber);
        } else {
            sb.insert(0, randomNumber + String.valueOf(separator));
        }

        String composed = sb.toString();

        // 4. Adjust to exact length
        if (composed.length() < length) {
            // Extend with secure random chars
            StringBuilder extension = new StringBuilder(composed);
            while (extension.length() < length) {
                extension.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
            }
            return extension.toString();
        } else if (composed.length() > length) {
            // Trim intelligently
            return composed.substring(0, length);
        }

        return composed;
    }

    private String mutateWordCase(String word) {
        if (word == null || word.isEmpty()) return "";
        // Randomly capitalize first letter, keep lowercase, or uppercase
        int action = random.nextInt(3);
        switch (action) {
            case 0: // Capitalize first letter
                return Character.toUpperCase(word.charAt(0)) + (word.length() > 1 ? word.substring(1).toLowerCase() : "");
            case 1: // Full uppercase
                return word.toUpperCase();
            default: // Full lowercase
                return word.toLowerCase();
        }
    }

    private String generateRandomFallback(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }
}
