package utils;

/**
 * Global Constants for SecurePass Vault application.
 */
public final class Constants {

    private Constants() {
        // Prevent instantiation
    }

    // File Paths
    public static final String VAULT_FILE = "resources/vault.dat";
    public static final String COMMON_PASSWORDS_FILE = "resources/common_passwords.txt";
    public static final String ENGLISH_WORDS_FILE = "resources/english_words.txt";

    // Cryptography Settings
    public static final String KEY_ALGORITHM = "AES";
    public static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    public static final int GCM_TAG_LENGTH_BITS = 128;
    public static final int GCM_IV_LENGTH_BYTES = 12;
    public static final String KDF_ALGORITHM = "PBKDF2WithHmacSHA256";
    public static final int PBKDF2_ITERATIONS = 65536;
    public static final int PBKDF2_KEY_LENGTH_BITS = 256;
    public static final int SALT_LENGTH_BYTES = 16;
    public static final String HASH_ALGORITHM = "SHA-256";

    // HIBP API Settings
    public static final String HIBP_API_URL = "https://api.pwnedpasswords.com/range/";
    public static final int HTTP_TIMEOUT_SECONDS = 5;

    // Security Thresholds
    public static final double ENTROPY_THRESHOLD_WEAK = 35.0;
    public static final double ENTROPY_THRESHOLD_MEDIUM = 60.0;
    public static final double ENTROPY_THRESHOLD_STRONG = 80.0;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final double SIMILARITY_THRESHOLD_PCT = 70.0;

    // Keyboard Patterns (Common row patterns)
    public static final String[] KEYBOARD_ROWS = {
        "qwertyuiop", "asdfghjkl", "zxcvbnm",
        "1234567890",
        "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"
    };

    // Console Colors (ANSI Escapes)
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_UNDERLINE = "\u001B[4m";
}
