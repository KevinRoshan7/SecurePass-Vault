package utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * File utility class utilizing Java NIO for safe file operations.
 */
public final class FileUtils {

    private FileUtils() {
        // Prevent instantiation
    }

    /**
     * Ensures that the directory structure for the given file path exists.
     */
    public static void ensureParentDirectoriesExist(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    /**
     * Reads all lines from a file path. If not found on disk, tries to load as resource from classloader.
     */
    public static List<String> readAllLines(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (Files.exists(path)) {
            return Files.readAllLines(path);
        }

        // Fallback to classloader stream (for resources folder packaged inside classpath)
        String resourcePath = filePath.startsWith("/") ? filePath : "/" + filePath;
        try (InputStream is = FileUtils.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                // If it doesn't exist, return empty list rather than crashing
                return new ArrayList<>();
            }
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is))) {
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                return lines;
            }
        }
    }

    /**
     * Safely checks if a file exists.
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Path.of(filePath));
    }
}
