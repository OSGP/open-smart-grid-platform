package org.opensmartgridplatform.secretmgmt.application.services.encryption.providers;

import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.Secret;

public interface EncryptionProvider {
    EncryptedSecret encrypt(Secret secret) throws Exception;
    Secret  decrypt(EncryptedSecret secret) throws Exception;
    EncryptionProviderType getType();
}

