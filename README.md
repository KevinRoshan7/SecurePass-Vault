# 🔐 SecurePass Vault

**A Local-First, Java-Based Password Management Application**

SecurePass Vault is a console-based password management application designed to securely store, manage, and audit credentials directly on the user's local machine. It follows a privacy-focused, local-first approach, ensuring that sensitive information remains under the user's control without relying on cloud storage, external servers, or third-party APIs.

The application uses cryptographic techniques to protect stored credentials and provides encrypted backup and restoration functionality for secure vault migration between devices.

---

## ✨ Key Features

- **Local Credential Management:** Store and manage credentials locally without cloud dependencies.
- **Secure Encryption:** Uses AES-GCM authenticated encryption to protect sensitive credential data.
- **Master Password Verification:** Uses SHA-256 hashing to verify the master password during vault access and restoration.
- **Encrypted Vault Backup:** Export credentials into a custom `.spv` (SecurePass Vault Backup) file.
- **Secure Vault Migration:** Transfer encrypted backups between computers or devices.
- **Data Integrity Verification:** AES-GCM authentication tags help detect modified or corrupted backup files.
- **Privacy-Focused Design:** No external servers, cloud storage, or third-party APIs are involved in the backup and restoration process.

---

## 🛡️ Security Architecture

SecurePass Vault follows a local-first security model in which the user's credentials remain on their own machine.

### Encryption

The application uses **AES-GCM (Advanced Encryption Standard – Galois/Counter Mode)** to encrypt credential data.

AES-GCM provides:
- Confidentiality through encryption.
- Integrity verification through authentication tags.
- Detection of unauthorized modifications to encrypted data.

### Password Verification

The application uses **SHA-256 hashing** to verify the master password during the vault restoration process.

The password is checked against the hash stored in the exported backup before attempting to decrypt the credential payload.

---

## 📦 Secure Vault Migration

SecurePass Vault supports exporting and importing encrypted vault backups, allowing users to migrate their credentials between devices without cloud synchronization.

### 1. Export Secure Vault

The export process:

1. Retrieves the credentials from the application's memory.
2. Serializes the credential data into a backup payload.
3. Encrypts the payload using AES-GCM.
4. Generates a custom `.spv` backup file containing the required metadata and encrypted credentials.

The backup includes:

| Component | Purpose |
|---|---|
| Master username | Identifies the vault owner |
| Unique salt | Used in the encryption workflow |
| SHA-256 password hash | Verifies the master password |
| Initialization Vector (IV) | Supports AES-GCM encryption |
| Encrypted payload | Contains the protected credentials |

### 2. Import Existing Vault

To restore a vault:

1. Select the previously exported `.spv` backup file.
2. Enter the master password used during export.
3. The application calculates the password hash and verifies it against the stored hash.
4. If verification succeeds, the encrypted payload is decrypted.
5. AES-GCM authentication verifies the integrity of the backup.
6. The restored credentials replace the active vault's credentials.

If password verification or integrity validation fails, the restoration process is rejected.

---

## 🔒 Security Design Principles

| Principle | Implementation |
|---|---|
| Local-first storage | Credentials remain on the user's machine |
| Encryption | AES-GCM authenticated encryption |
| Password verification | SHA-256 hashing |
| Backup protection | Encrypted `.spv` backup files |
| Integrity validation | AES-GCM authentication tags |
| No cloud dependency | No external services for backup or restoration |

### Important Security Warning

Anyone possessing both the exported `.spv` backup file and the corresponding master password may be able to restore and access the vault.

Keep backup files in trusted locations and protect the master password carefully.

---

## 🧰 Technology Overview

- **Programming Language:** Java
- **Cryptography:** AES-GCM
- **Hashing:** SHA-256
- **Application Type:** Console-Based Application
- **Storage Approach:** Local-first
- **Backup Format:** Custom `.spv` file

---

## 🎯 Project Objectives

- Demonstrate practical application of Java programming concepts.
- Understand encryption and password verification techniques.
- Protect sensitive credentials through authenticated encryption.
- Implement secure backup and restoration workflows.
- Explore data integrity and privacy-focused application design.

---

## 🚀 Future Enhancements

- Password strength analysis.
- Secure password generation.
- Improved master-password protection.
- Additional backup validation and recovery options.
- Enhanced credential organization and search functionality.

---

## 👥 Contributors

- **Hariharan D**
- **Kevin Roshan**

---

**SecurePass Vault — Control Comes Responsibility.**

A privacy-focused approach to credential management, built around local control and secure storage.
