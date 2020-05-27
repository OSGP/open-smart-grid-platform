package org.opensmartgridplatform.secretmgmt.application.services.encryption.providers;

/**
 * Specifies which encryption/decryption provider is to be used.
 * HSM, Thales Hardware Security Module.
 * JRE, Sun Java Runtime Environment's crypto implementation.
 */
public enum EncryptionProviderType {
    HSM, //Thales HSM
    JRE  //Sun crypto
}





