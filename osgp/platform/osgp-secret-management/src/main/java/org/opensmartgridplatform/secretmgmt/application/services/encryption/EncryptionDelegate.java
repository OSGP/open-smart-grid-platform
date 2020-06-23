package org.opensmartgridplatform.secretmgmt.application.services.encryption;

import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;

public interface EncryptionDelegate {
    EncryptedSecret encrypt(EncryptionProviderType encryptionProviderType, Secret secret, String keyReference);
    Secret decrypt(EncryptedSecret secret, String keyReference);
}
