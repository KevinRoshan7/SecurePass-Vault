package analyzer;

import utils.Constants;

/**
 * Detects structural patterns within passwords (repetition, sequences, keyboard layouts).
 */
public class PatternChecker {

    /**
     * Checks for consecutive identical characters (e.g., "aaaa" or "1111").
     * Returns true if a character is repeated 3 or more times consecutively.
     */
    public boolean hasRepeatedCharacters(String password) {
        if (password == null || password.length() < 3) return false;

        for (int i = 2; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c == password.charAt(i - 1) && c == password.charAt(i - 2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks for sequential characters in ASCII/Unicode sequence (e.g., "abc", "789", "cba").
     * Returns true if 3 or more characters are sequential.
     */
    public boolean hasSequentialCharacters(String password) {
        if (password == null || password.length() < 3) return false;

        for (int i = 2; i < password.length(); i++) {
            char c1 = password.charAt(i - 2);
            char c2 = password.charAt(i - 1);
            char c3 = password.charAt(i);

            // Ascending sequence (e.g., a-b-c)
            if (c2 == c1 + 1 && c3 == c2 + 1) {
                return true;
            }
            // Descending sequence (e.g., c-b-a)
            if (c2 == c1 - 1 && c3 == c2 - 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks for keyboard row pattern substrings of length 3 or more (e.g., "qwe", "asdf").
     */
    public boolean hasKeyboardPattern(String password) {
        if (password == null || password.length() < 3) return false;

        String lowerPassword = password.toLowerCase();

        for (String row : Constants.KEYBOARD_ROWS) {
            // Check substrings of length 3 in the keyboard row
            for (int i = 0; i <= row.length() - 3; i++) {
                String sub = row.substring(i, i + 3);
                if (lowerPassword.contains(sub)) {
                    return true;
                }
            }
        }
        return false;
    }
}
