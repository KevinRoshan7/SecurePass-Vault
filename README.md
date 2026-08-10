# SecurePass Vault

SecurePass Vault is a robust, local-first console application designed to help you securely store, manage, and audit your credentials. Built with modern cryptographic practices, it ensures your sensitive data is kept private and safe right on your own machine.

## Philosophy

### Control Comes Responsibility
SecurePass Vault never connects to the internet or uploads your data to the cloud. You are fully in control of your vault, which means you are also responsible for its backups, security, and migration.

---

## Secure Vault Migration

SecurePass Vault follows a local-first approach to vault migration. Users can securely migrate their encrypted vault between different computers or devices without introducing any cloud storage or automatic synchronization.

### How it Works
1. **Export Secure Vault**: Decrypts the credentials from memory and serializes them into a custom `.spv` (SecurePass Vault Backup) file. The file itself is encrypted using AES-GCM, incorporating:
   - Plaintext master username (to confirm identity).
   - Plaintext unique salt (to derive the AES key).
   - Plaintext SHA-256 hash of the master password (to verify the password).
   - Plaintext unique Initialization Vector (IV).
   - The encrypted credentials payload.
2. **Import Existing Vault**: Allows you to restore an exported `.spv` file to your active local registry. You must supply the master password that was used during export. The app hashes your password, validates it against the stored hash, decrypts the payload, verifies file integrity, and replaces the current vault's credentials.

### Key Security Design Properties
- **No Cloud Services**: No servers or external APIs are used during the backup or restore process.
- **No Plaintext Passwords**: Plaintext credentials are never written to disk. The `.spv` file remains fully encrypted at all times.
- **Robust Integrity Verification**: AES-GCM authentication tags verify the integrity of the backup file. If the file is altered or corrupted, import fails gracefully.
- **Pre-Decryption Authentication Verification**: The password hash is checked first, allowing the app to clearly distinguish between a wrong master password and a corrupted/altered backup file.

> [!WARNING]
> Anyone possessing BOTH the exported `.spv` backup file AND your master password can restore and access your vault. Store this file only in a trusted location.
>
> **Control Comes Responsibility.**

