package org.opensmartgridplatform.secretmgmt.application.services.encryption.providers;

import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.Secret;

import java.io.File;

public interface EncryptionProvider {
    EncryptedSecret encrypt(Secret secret, String keyReference);
    Secret  decrypt(EncryptedSecret secret, String keyReference);
    void    setKeyFile(File keystore);
    EncryptionProviderType getType();
}

