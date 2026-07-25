
package analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import utils.Constants;
import utils.FileUtils;

/**
 * Checks passwords against dictionaries and common password lists.
 */
public class DictionaryChecker {

    private final Set<String> commonPasswords = new HashSet<>();
    private final List<String> englishWords = new ArrayList<>();

    public DictionaryChecker() {
        loadDictionaries();
    }

    private void loadDictionaries() {
        try {
            // Load common passwords
            List<String> commonList = FileUtils.readAllLines(Constants.COMMON_PASSWORDS_FILE);
            for (String pass : commonList) {
                commonPasswords.add(pass.strip().toLowerCase());
            }

            // Load english dictionary words
            List<String> englishList = FileUtils.readAllLines(Constants.ENGLISH_WORDS_FILE);
            for (String word : englishList) {
                String cleanWord = word.strip().toLowerCase();
                if (cleanWord.length() >= 3) { // Only check words of length 3 or more
                    englishWords.add(cleanWord);
                }
            }
        } catch (IOException e) {
            // Fallback: If resources are missing, proceed with empty lists
            System.err.println("Warning: Dictionary files could not be loaded. Offline check skipped.");
        }
    }

    /**
     * Checks if the password matches a common weak password.
     */
    public boolean isCommonPassword(String password) {
        if (password == null) return false;
        return commonPasswords.contains(password.toLowerCase());
    }

    /**
     * Checks if the password contains a common English word (length >= 4) as a substring.
     */
    public boolean containsDictionaryWord(String password) {
        if (password == null || password.length() < 4) return false;
        
        String lowerPassword = password.toLowerCase();
        for (String word : englishWords) {
            if (word.length() >= 4 && lowerPassword.contains(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a matching dictionary word from the password if one is found.
     */
    public String getMatchingDictionaryWord(String password) {
        if (password == null || password.length() < 4) return null;
        
        String lowerPassword = password.toLowerCase();
        for (String word : englishWords) {
            if (word.length() >= 4 && lowerPassword.contains(word)) {
                return word;
            }
        }
        return null;
    }
}
