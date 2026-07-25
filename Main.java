import authentication.AuthenticationManager;
import authentication.Session;
import analyzer.PasswordAnalyzer;
import analyzer.StrengthReport;
import breach.BreachChecker;
import breach.BreachResult;
import dashboard.Dashboard;
import generator.PersonalizedPasswordGenerator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.DashboardData;
import models.PasswordResult;
import similarity.SimilarityResult;
import storage.Credential;
import storage.VaultManager;
import utils.ConsoleUI;
import utils.Constants;
import utils.Validators;

/**
 * Entry point and orchestrator for SecurePass Vault console application.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static Session activeSession = null;
    private static VaultManager vaultManager = null;

    private static final PasswordAnalyzer analyzer = new PasswordAnalyzer();
    private static final BreachChecker breachChecker = new BreachChecker();
    private static final PersonalizedPasswordGenerator generator = new PersonalizedPasswordGenerator();
    private static final Dashboard dashboard = new Dashboard();

    public static void main(String[] args) {
        ConsoleUI.clearScreen();
        boolean running = true;

        while (running) {
            if (activeSession == null) {
                running = showAuthMenu();
            } else {
                showMainMenu();
            }
        }
        System.out.println(Constants.ANSI_CYAN + "\nThank you for using SecurePass Vault. Keep your credentials secure!" + Constants.ANSI_RESET);
    }

    /**
     * Renders registration and login options.
     */
    private static boolean showAuthMenu() {
        ConsoleUI.printHeader("SECUREPASS VAULT");
        ConsoleUI.printMenuOption("1", "Login");
        ConsoleUI.printMenuOption("2", "Register Master Account");
        ConsoleUI.printMenuOption("3", "Exit");
        System.out.print("\nChoose an option (1-3): ");
        String choice = scanner.nextLine().strip();

        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "2":
                handleRegistration();
                break;
            case "3":
                return false;
            default:
                ConsoleUI.printError("Invalid Choice", "Please enter a number between 1 and 3.");
        }
        return true;
    }

    /**
     * Renders primary features menu once authenticated.
     */
    private static void showMainMenu() {
        ConsoleUI.printHeader("MAIN MENU (Logged in: " + activeSession.getUsername() + ")");
        ConsoleUI.printMenuOption("1", "Store Credential");
        ConsoleUI.printMenuOption("2", "View / Manage Credentials");
        ConsoleUI.printMenuOption("3", "Analyze Password");
        ConsoleUI.printMenuOption("4", "Intelligent Password Generator");
        ConsoleUI.printMenuOption("5", "Similarity Audit Report");
        ConsoleUI.printMenuOption("6", "Security Dashboard");
        ConsoleUI.printMenuOption("7", "Logout");
        System.out.print("\nChoose an option (1-7): ");
        String choice = scanner.nextLine().strip();

        switch (choice) {
            case "1":
                handleStoreCredential();
                break;
            case "2":
                handleViewCredentials();
                break;
            case "3":
                handleAnalyzePassword();
                break;
            case "4":
                handleGeneratePassword();
                break;
            case "5":
                handleSimilarityReport();
                break;
            case "6":
                handleDashboard();
                break;
            case "7":
                handleLogout();
                break;
            default:
                ConsoleUI.printError("Invalid Choice", "Please enter a number between 1 and 7.");
        }
    }

    private static void handleRegistration() {
        ConsoleUI.printHeader("ACCOUNT REGISTRATION");
        System.out.print("Enter new Master Username (3-20 alphanumeric characters): ");
        String username = scanner.nextLine().strip();
        if (!Validators.isValidUsername(username)) {
            ConsoleUI.printError("Invalid Username", "Must be 3-20 characters, alphanumeric, dots, or underscores.");
            return;
        }

        System.out.print("Enter new Master Password: ");
        String password = scanner.nextLine();
        if (password.length() < 8) {
            ConsoleUI.printWarning("Weak Password", "For security, master passwords must be at least 8 characters.");
        }

        try {
            boolean success = AuthenticationManager.register(username, password);
            if (success) {
                ConsoleUI.printSuccess("Registration Complete", "Account created successfully. You can now login.");
            } else {
                ConsoleUI.printError("Registration Failed", "Username already exists. Choose a different username.");
            }
        } catch (Exception e) {
            ConsoleUI.printError("System Error", "An error occurred during registration: " + e.getMessage());
        }
    }

    private static void handleLogin() {
        ConsoleUI.printHeader("LOGIN AUTHENTICATION");
        System.out.print("Master Username: ");
        String username = scanner.nextLine().strip();
        System.out.print("Master Password: ");
        String password = scanner.nextLine();

        try {
            activeSession = AuthenticationManager.login(username, password);
            if (activeSession != null) {
                // Initialize the vault manager with session key
                vaultManager = new VaultManager(activeSession.getUsername(), activeSession.getSessionKey());
                ConsoleUI.printSuccess("Access Granted", "Logged in. Vault loaded successfully.");
            } else {
                ConsoleUI.printError("Access Denied", "Incorrect username or master password.");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Decryption Failed", "Access denied. (Verify credentials, or vault database might be corrupt).");
        }
    }

    private static void handleLogout() {
        AuthenticationManager.logout(activeSession);
        activeSession = null;
        vaultManager = null;
        ConsoleUI.printInfo("Session Closed", "Logged out. Session key erased from memory.");
    }

    private static void handleStoreCredential() {
        ConsoleUI.printHeader("STORE CREDENTIAL");
        System.out.print("Website/Domain (e.g., google.com): ");
        String website = scanner.nextLine().strip();
        if (!Validators.isValidWebsite(website)) {
            ConsoleUI.printError("Invalid Domain", "Please enter a valid website name or domain address.");
            return;
        }

        System.out.print("Username/Email: ");
        String username = scanner.nextLine().strip();
        if (username.isEmpty()) {
            ConsoleUI.printError("Missing Field", "Username cannot be empty.");
            return;
        }

        System.out.print("Category (e.g., Work, Social, Banking): ");
        String category = scanner.nextLine().strip();
        if (category.isEmpty()) category = "General";

        System.out.println("\nPassword options:");
        System.out.println("  1. Enter Password Manually");
        System.out.println("  2. Intelligently Generate Memorable Password");
        System.out.print("Choose option (1-2): ");
        String passChoice = scanner.nextLine().strip();

        String finalPassword = "";
        if ("2".equals(passChoice)) {
            finalPassword = generatePasswordInteractive();
            if (finalPassword == null) return;
        } else {
            System.out.print("Enter password: ");
            finalPassword = scanner.nextLine();
            
            // Run checks on manual password
            StrengthReport report = analyzer.analyze(finalPassword);
            BreachResult breach = breachChecker.checkPassword(finalPassword);
            
            displayPasswordSecurityAlerts(report, breach);
        }

        try {
            Credential credential = new Credential(
                    website, username, finalPassword, category,
                    LocalDateTime.now(), LocalDateTime.now()
            );
            vaultManager.addCredential(credential);
            ConsoleUI.printSuccess("Credential Saved", "Credential for " + website + " encrypted and stored successfully.");
        } catch (IOException e) {
            ConsoleUI.printError("Save Failure", "Could not persist credential to vault.dat: " + e.getMessage());
        }
    }

    private static String generatePasswordInteractive() {
        System.out.print("Desired password length (min 12): ");
        int length;
        try {
            length = Integer.parseInt(scanner.nextLine().strip());
            if (length < 12) {
                ConsoleUI.printWarning("Length Adjustment", "Automatically increasing length to 12 for security.");
                length = 12;
            }
        } catch (NumberFormatException e) {
            ConsoleUI.printError("Invalid Length", "Using default length of 16.");
            length = 16;
        }

        System.out.println("Enter memorable preferred words separated by space (e.g., river falcon sky):");
        String wordLine = scanner.nextLine().strip();
        List<String> words = new ArrayList<>();
        if (!wordLine.isEmpty()) {
            for (String w : wordLine.split("\\s+")) {
                if (w.length() >= 2) words.add(w);
            }
        }

        System.out.println("\nGenerating secure, breach-free password. Please wait...");
        PasswordResult res = generator.generatePassword(length, words);

        if (res.getPassword() == null) {
            ConsoleUI.printError("Generation Failure", "Could not compose a secure password with those words. Try again.");
            return null;
        }

        System.out.println("\nGenerated Password: " + Constants.ANSI_GREEN + Constants.ANSI_BOLD + res.getPassword() + Constants.ANSI_RESET);
        System.out.println("Strength Score: " + res.getStrengthReport().getScore() + "/100 (" + res.getStrengthReport().getRiskLevel() + ")");
        System.out.println("Breach Status: " + res.getBreachResult().getRecommendation());

        System.out.print("\nUse this password? (Y/N): ");
        String confirm = scanner.nextLine().strip();
        if (confirm.equalsIgnoreCase("Y")) {
            return res.getPassword();
        }
        return null;
    }

    private static void handleViewCredentials() {
        ConsoleUI.printHeader("VIEW CREDENTIALS");
        System.out.print("Enter search keyword (or press Enter to list all): ");
        String query = scanner.nextLine().strip();

        List<Credential> matches = vaultManager.searchCredentials(query);
        if (matches.isEmpty()) {
            ConsoleUI.printInfo("No Results", "No credentials match your request.");
            return;
        }

        System.out.println("\nStored Credentials:");
        int w1 = 5, w2 = 20, w3 = 25;
        String border = "├" + "─".repeat(w1 + 2) + "┼" + "─".repeat(w2 + 2) + "┼" + "─".repeat(w3 + 2) + "┤";
        
        System.out.println("┌" + "─".repeat(w1 + 2) + "┬" + "─".repeat(w2 + 2) + "┬" + "─".repeat(w3 + 2) + "┐");
        ConsoleUI.printTableRow("Index", w1, "Website", w2, "Username / Category", w3);
        System.out.println(border);

        for (int i = 0; i < matches.size(); i++) {
            Credential cred = matches.get(i);
            String desc = cred.getUsername() + " (" + cred.getCategory() + ")";
            ConsoleUI.printTableRow(String.valueOf(i + 1), w1, cred.getWebsite(), w2, desc, w3);
        }
        System.out.println("└" + "─".repeat(w1 + 2) + "┴" + "─".repeat(w2 + 2) + "┴" + "─".repeat(w3 + 2) + "┘");

        System.out.print("\nEnter index number to view full details (or 0 to cancel): ");
        String indexStr = scanner.nextLine().strip();
        int index;
        try {
            index = Integer.parseInt(indexStr) - 1;
            if (index == -1) return;
            if (index < 0 || index >= matches.size()) {
                ConsoleUI.printError("Invalid Index", "Out of bounds.");
                return;
            }
        } catch (NumberFormatException e) {
            ConsoleUI.printError("Invalid Input", "Please enter a valid index number.");
            return;
        }

        Credential cred = matches.get(index);
        
        // Find index in actual vaultManager list
        int actualIndex = vaultManager.getCredentials().indexOf(cred);

        displayCredentialDetails(cred, actualIndex);
    }

    private static void displayCredentialDetails(Credential cred, int actualIndex) {
        ConsoleUI.printHeader("CREDENTIAL DETAILS");
        System.out.println("  Website/URL: " + Constants.ANSI_CYAN + cred.getWebsite() + Constants.ANSI_RESET);
        System.out.println("  Username:    " + cred.getUsername());
        System.out.println("  Password:    " + Constants.ANSI_YELLOW + Constants.ANSI_BOLD + cred.getPassword() + Constants.ANSI_RESET);
        System.out.println("  Category:    " + cred.getCategory());
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("  Created:     " + cred.getCreatedDate().format(dtf));
        System.out.println("  Modified:    " + cred.getModifiedDate().format(dtf));

        // Display quick audits
        StrengthReport report = analyzer.analyze(cred.getPassword());
        BreachResult breach = breachChecker.checkPassword(cred.getPassword());
        System.out.println("  Strength:    " + report.getScore() + "/100 (" + report.getRiskLevel() + ")");
        System.out.println("  Breach:      " + breach.getRecommendation());

        System.out.println("\nOptions:");
        System.out.println("  1. Update Password / Category");
        System.out.println("  2. Delete Credential");
        System.out.println("  3. Back");
        System.out.print("Choose option (1-3): ");
        String subChoice = scanner.nextLine().strip();

        switch (subChoice) {
            case "1":
                handleUpdateCredential(actualIndex);
                break;
            case "2":
                handleDeleteCredential(actualIndex);
                break;
            default:
                break;
        }
    }

    private static void handleUpdateCredential(int index) {
        ConsoleUI.printHeader("UPDATE PASSWORD/CATEGORY");
        System.out.println("Enter options:");
        System.out.println("  1. Enter New Password Manually");
        System.out.println("  2. Intelligently Generate New Password");
        System.out.print("Choose option (1-2): ");
        String passChoice = scanner.nextLine().strip();

        String newPassword = "";
        if ("2".equals(passChoice)) {
            newPassword = generatePasswordInteractive();
            if (newPassword == null) return;
        } else {
            System.out.print("New Password: ");
            newPassword = scanner.nextLine();
            displayPasswordSecurityAlerts(analyzer.analyze(newPassword), breachChecker.checkPassword(newPassword));
        }

        System.out.print("New Category (or press Enter to keep current): ");
        String newCategory = scanner.nextLine().strip();
        if (newCategory.isEmpty()) {
            newCategory = vaultManager.getCredentials().get(index).getCategory();
        }

        try {
            vaultManager.updateCredential(index, newPassword, newCategory);
            ConsoleUI.printSuccess("Update Complete", "Credential updated and encrypted.");
        } catch (IOException e) {
            ConsoleUI.printError("Save Failure", "Could not persist update: " + e.getMessage());
        }
    }

    private static void handleDeleteCredential(int index) {
        System.out.print("Are you absolutely sure you want to delete this credential? (Y/N): ");
        String confirm = scanner.nextLine().strip();
        if (confirm.equalsIgnoreCase("Y")) {
            try {
                vaultManager.deleteCredential(index);
                ConsoleUI.printSuccess("Deleted", "Credential deleted successfully.");
            } catch (IOException e) {
                ConsoleUI.printError("Delete Failure", "Could not delete from vault.dat: " + e.getMessage());
            }
        }
    }

    private static void handleAnalyzePassword() {
        ConsoleUI.printHeader("PASSWORD SECURITY ANALYZER");
        System.out.print("Enter password to analyze: ");
        String password = scanner.nextLine();

        System.out.println("\nAnalyzing structure and breach databases. Please wait...");
        StrengthReport report = analyzer.analyze(password);
        BreachResult breach = breachChecker.checkPassword(password);

        ConsoleUI.printHeader("SECURITY AUDIT REPORT");
        System.out.println("  Password Analyzed:  " + Constants.ANSI_YELLOW + password + Constants.ANSI_RESET);
        
        // Colorized score
        String scoreColor = Constants.ANSI_GREEN;
        if (report.getScore() < 35) scoreColor = Constants.ANSI_RED;
        else if (report.getScore() < 70) scoreColor = Constants.ANSI_YELLOW;
        
        System.out.println("  Overall Score:      " + scoreColor + report.getScore() + "/100" + Constants.ANSI_RESET);
        System.out.println("  Risk Level:         " + scoreColor + report.getRiskLevel() + Constants.ANSI_RESET);
        System.out.printf("  Brute-force Entropy: %.2f bits (Pool Size)%n", report.getPoolEntropy());
        System.out.printf("  Shannon Randomness:  %.2f bits (Information Density)%n", report.getShannonEntropy());
        System.out.println("  Breach Compromises: " + (breach.isBreached() ? Constants.ANSI_RED + breach.getBreachCount() + " times" : Constants.ANSI_GREEN + "0 occurrences") + Constants.ANSI_RESET);

        // Checklist
        System.out.println("\n  Requirements Checklist:");
        System.out.println("    " + (report.isHasMinLength() ? "✔" : "✘") + " Length (8+ characters)");
        System.out.println("    " + (report.isHasUppercase() ? "✔" : "✘") + " Uppercase characters");
        System.out.println("    " + (report.isHasLowercase() ? "✔" : "✘") + " Lowercase characters");
        System.out.println("    " + (report.isHasDigit() ? "✔" : "✘") + " Digit character");
        System.out.println("    " + (report.isHasSpecial() ? "✔" : "✘") + " Special symbol");

        // Warnings and recommendations
        if (!report.getWarnings().isEmpty()) {
            System.out.println("\n  Structural Weaknesses Identified:");
            for (String warn : report.getWarnings()) {
                System.out.println(Constants.ANSI_RED + "    • " + warn + Constants.ANSI_RESET);
            }
        }

        if (!report.getSuggestions().isEmpty() || breach.isBreached()) {
            System.out.println("\n  Remedy Action Items:");
            if (breach.isBreached()) {
                System.out.println(Constants.ANSI_YELLOW + "    • " + breach.getRecommendation() + Constants.ANSI_RESET);
            }
            for (String sug : report.getSuggestions()) {
                System.out.println(Constants.ANSI_YELLOW + "    • " + sug + Constants.ANSI_RESET);
            }
        }
        System.out.println();
    }

    private static void handleGeneratePassword() {
        ConsoleUI.printHeader("PASSWORD GENERATOR");
        System.out.print("Desired length (min 12): ");
        int length;
        try {
            length = Integer.parseInt(scanner.nextLine().strip());
        } catch (NumberFormatException e) {
            length = 16;
        }
        
        System.out.println("Enter memorable preferred words separated by space (optional):");
        String wordLine = scanner.nextLine().strip();
        List<String> words = new ArrayList<>();
        if (!wordLine.isEmpty()) {
            for (String w : wordLine.split("\\s+")) {
                words.add(w);
            }
        }

        PasswordResult res = generator.generatePassword(length, words);
        
        ConsoleUI.printSuccess("Generated Password", res.getPassword());
        System.out.println("  Strength Score: " + res.getStrengthReport().getScore() + "/100 (" + res.getStrengthReport().getRiskLevel() + ")");
        System.out.println("  Leaked Checks:  " + res.getBreachResult().getRecommendation());
    }

    private static void handleSimilarityReport() {
        ConsoleUI.printHeader("SIMILARITY AUDIT");
        List<Credential> list = vaultManager.getCredentials();
        if (list.size() < 2) {
            ConsoleUI.printInfo("Audit Skipped", "Similarity checks require at least 2 credentials in vault.");
            return;
        }

        System.out.println("Analyzing password combinations via Levenshtein Edit Distance. Please wait...");
        similarity.PasswordSimilarityDetector detector = new similarity.PasswordSimilarityDetector();
        List<SimilarityResult> results = detector.detectSimilarities(list);

        if (results.isEmpty()) {
            ConsoleUI.printSuccess("Clean Audit", "No similar or reused passwords detected. Excellent hygiene!");
            return;
        }

        System.out.println("\nIdentified Similarity Groups:");
        for (SimilarityResult res : results) {
            String color = Constants.ANSI_YELLOW;
            if ("CRITICAL".equals(res.getRiskLevel())) color = Constants.ANSI_RED;

            System.out.println(color + "┌──────────────────────────────────────────────────────────" + Constants.ANSI_RESET);
            System.out.printf("│ Ratio: %.1f%% | Risk: %s | Distance: %d%n", res.getSimilarityPercentage(), res.getRiskLevel(), res.getDistance());
            System.out.println("│ Account 1: " + res.getCredential1().getWebsite() + " (" + res.getCredential1().getUsername() + ")");
            System.out.println("│ Account 2: " + res.getCredential2().getWebsite() + " (" + res.getCredential2().getUsername() + ")");
            System.out.println("│ Remedy:    " + res.getRecommendation());
            System.out.println(color + "└──────────────────────────────────────────────────────────" + Constants.ANSI_RESET);
        }
    }

    private static void handleDashboard() {
        ConsoleUI.printHeader("SECURITY DASHBOARD");
        List<Credential> credentials = vaultManager.getCredentials();
        
        DashboardData data = dashboard.compileData(credentials);

        // Security Score rendering
        String scoreColor = Constants.ANSI_GREEN;
        if (data.getOverallScore() < 50) scoreColor = Constants.ANSI_RED;
        else if (data.getOverallScore() < 80) scoreColor = Constants.ANSI_YELLOW;

        System.out.println("\n  OVERALL VAULT HYGIENE SCORE: " + scoreColor + Constants.ANSI_BOLD + data.getOverallScore() + "/100" + Constants.ANSI_RESET);
        
        // Progress bar simulation
        int blocks = data.getOverallScore() / 5;
        System.out.print("  [");
        System.out.print(scoreColor + "█".repeat(blocks) + Constants.ANSI_RESET);
        System.out.print(" ".repeat(20 - blocks));
        System.out.println("]");

        System.out.println("\n  Postures Summary:");
        System.out.println("    • Total Stored Credentials: " + data.getTotalCredentials());
        System.out.println("    • Strong Passwords:         " + Constants.ANSI_GREEN + data.getStrongCount() + Constants.ANSI_RESET);
        System.out.println("    • Medium Passwords:         " + Constants.ANSI_YELLOW + data.getMediumCount() + Constants.ANSI_RESET);
        System.out.println("    • Weak Passwords:           " + Constants.ANSI_RED + data.getWeakCount() + Constants.ANSI_RESET);
        System.out.println("    • Breached Passwords:       " + Constants.ANSI_RED + data.getBreachedCount() + Constants.ANSI_RESET);
        System.out.printf("    • Average Entropy:          %.2f bits%n", data.getAverageEntropy());
        System.out.println("    • Similarity Pair Alarms:   " + data.getSimilarityResults().size());

        if (!data.getRecommendations().isEmpty()) {
            System.out.println("\n  Actionable Security Recommendations:");
            for (String rec : data.getRecommendations()) {
                System.out.println("    • " + rec);
            }
        }
        System.out.println();
    }

    private static void displayPasswordSecurityAlerts(StrengthReport report, BreachResult breach) {
        if (report.getScore() < 60 || breach.isBreached()) {
            System.out.println(Constants.ANSI_RED + "\n[!] Security Warning: Low Password Health!" + Constants.ANSI_RESET);
            System.out.println("    Strength Score: " + report.getScore() + "/100 (" + report.getRiskLevel() + ")");
            System.out.println("    Breach Alerts:  " + breach.getRecommendation());
            System.out.print("Continue saving this password anyway? (Y/N): ");
            String ans = scanner.nextLine().strip();
            if (!ans.equalsIgnoreCase("Y")) {
                ConsoleUI.printInfo("Action Aborted", "Credential storage cancelled.");
                return;
            }
        }
    }
}
