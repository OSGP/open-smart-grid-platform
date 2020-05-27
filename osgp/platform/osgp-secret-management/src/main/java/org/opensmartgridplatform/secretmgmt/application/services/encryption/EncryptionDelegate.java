package org.opensmartgridplatform.secretmgmt.application.services.encryption;

import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;

public interface EncryptionDelegate {
    EncryptedSecret encrypt(EncryptionProviderType encType, Secret secret) throws Exception;
    Secret decrypt(EncryptedSecret secret) throws Exception;
}
