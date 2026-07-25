package utils;

import static utils.Constants.*;

/**
 * Utility for polished console output including borders, boxes, colors, and layouts.
 */
public final class ConsoleUI {

    private ConsoleUI() {
        // Prevent instantiation
    }

    /**
     * Clears the console screen using ANSI codes.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Prints a stylized header banner.
     */
    public static void printHeader(String title) {
        int width = 60;
        String line = "═".repeat(width);
        
        System.out.println(ANSI_CYAN + ANSI_BOLD + "╔" + line + "╗" + ANSI_RESET);
        
        // Center text
        int padding = (width - title.length()) / 2;
        String leftPad = " ".repeat(padding);
        String rightPad = " ".repeat(width - title.length() - padding);
        
        System.out.println(ANSI_CYAN + ANSI_BOLD + "║" + leftPad + ANSI_YELLOW + title + ANSI_CYAN + rightPad + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + ANSI_BOLD + "╚" + line + "╝" + ANSI_RESET);
    }

    /**
     * Prints a card bordered message.
     */
    public static void printMessageCard(String title, String message, String color) {
        int width = 58;
        String horiz = "─".repeat(width);
        System.out.println(color + "┌" + horiz + "┐" + ANSI_RESET);
        System.out.println(color + "│ " + ANSI_BOLD + title + " ".repeat(width - title.length() - 1) + color + "│" + ANSI_RESET);
        System.out.println(color + "├" + horiz + "┤" + ANSI_RESET);
        
        // Wrap or truncate message lines to fit width
        String[] words = message.split(" ");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > width - 4) {
                String lineText = currentLine.toString();
                System.out.println(color + "│  " + ANSI_RESET + lineText + " ".repeat(width - lineText.length() - 3) + color + "│" + ANSI_RESET);
                currentLine = new StringBuilder(word);
            } else {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            }
        }
        if (currentLine.length() > 0) {
            String lineText = currentLine.toString();
            System.out.println(color + "│  " + ANSI_RESET + lineText + " ".repeat(width - lineText.length() - 3) + color + "│" + ANSI_RESET);
        }
        
        System.out.println(color + "└" + horiz + "┘" + ANSI_RESET);
    }

    public static void printSuccess(String title, String message) {
        printMessageCard(title, message, ANSI_GREEN);
    }

    public static void printWarning(String title, String message) {
        printMessageCard(title, message, ANSI_YELLOW);
    }

    public static void printError(String title, String message) {
        printMessageCard(title, message, ANSI_RED);
    }

    public static void printInfo(String title, String message) {
        printMessageCard(title, message, ANSI_CYAN);
    }

    /**
     * Prints a menu prompt.
     */
    public static void printMenuOption(String index, String label) {
        System.out.println("  " + ANSI_CYAN + ANSI_BOLD + index + ". " + ANSI_RESET + label);
    }

    /**
     * Prints a formatted table row.
     */
    public static void printTableRow(String col1, int w1, String col2, int w2, String col3, int w3) {
        String f1 = "%-" + w1 + "s";
        String f2 = "%-" + w2 + "s";
        String f3 = "%-" + w3 + "s";
        String format = "│ " + f1 + " │ " + f2 + " │ " + f3 + " │";
        System.out.printf(format + "%n", truncate(col1, w1), truncate(col2, w2), truncate(col3, w3));
    }

    private static String truncate(String str, int maxLen) {
        if (str == null) return "";
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 3) + "...";
    }
}
