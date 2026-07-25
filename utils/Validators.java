package utils;

import java.util.regex.Pattern;

/**
 * Standard utility class for data validation using Regular Expressions and type checks.
 */
public final class Validators {

    private Validators() {
        // Prevent instantiation
    }

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
    private static final Pattern WEBSITE_PATTERN = Pattern.compile("^(https?://)?([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,63})(:[0-9]+)?(/.*)?$");

    /**
     * Validates if a username meets security and format requirements.
     * Alphanumeric, dots, underscores, dashes, 3-20 characters.
     */
    public static boolean isValidUsername(String username) {
        if (username == null) return false;
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validates if a website domain/URL is correctly structured.
     */
    public static boolean isValidWebsite(String website) {
        if (website == null || website.isBlank()) return false;
        // Basic check: either matches the URL pattern, or is a plain name without spaces
        return WEBSITE_PATTERN.matcher(website).matches() || (!website.contains(" ") && website.length() >= 3);
    }

    /**
     * Checks if a string is non-empty and non-blank.
     */
    public static boolean isNotEmpty(String input) {
        return input != null && !input.strip().isEmpty();
    }

    /**
     * Checks if a choice string is a valid integer within a specific range.
     */
    public static boolean isValidChoice(String choiceStr, int min, int max) {
        try {
            int choice = Integer.parseInt(choiceStr.strip());
            return choice >= min && choice <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
