package storage;

import authentication.User;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import utils.Constants;
import utils.FileUtils;

/**
 * Persists the registry map (containing all users and encrypted credentials) to vault.dat.
 */
public class FileManager {

    /**
     * Loads the user database map from vault.dat.
     * If the file does not exist, returns an empty map.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, User> loadUserRegistry() {
        Path path = Path.of(Constants.VAULT_FILE);
        if (!Files.exists(path)) {
            return new HashMap<>();
        }

        try (InputStream is = Files.newInputStream(path);
             ObjectInputStream ois = new ObjectInputStream(is)) {
            
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                return (Map<String, User>) obj;
            }
        } catch (FileNotFoundException e) {
            // Ignore, file doesn't exist yet
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Warning: Corrupted vault file detected. Initializing a new database.");
        }
        return new HashMap<>();
    }

    /**
     * Saves the user database map to vault.dat.
     */
    public static synchronized void saveUserRegistry(Map<String, User> registry) throws IOException {
        Path path = Path.of(Constants.VAULT_FILE);
        FileUtils.ensureParentDirectoriesExist(path);

        try (OutputStream os = Files.newOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(os)) {
            
            oos.writeObject(registry);
            oos.flush();
        } catch (IOException e) {
            throw new IOException("Failed to write data to SecurePass Vault (" + Constants.VAULT_FILE + ")", e);
        }
    }
}
