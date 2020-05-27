package org.opensmartgridplatform.secretmgmt.application.services.encryption;

import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;

/**
 * Secret class to store any binary encrypted secret. The secret can be anything.
 * A secret contains the type of encryption provider that was used to encrypt it.
 * There is no encoding/decoding.
 */
public class EncryptedSecret {
    private byte[] secret;
    private EncryptionProviderType type;

    public EncryptedSecret(EncryptionProviderType type, byte[] secret) {
        this.type = type;
        this.secret = secret;
    }

    public byte[] getSecret() {
        return secret;
    }

    public EncryptionProviderType getEncryptionProviderType() {
        return type;
    }
}
