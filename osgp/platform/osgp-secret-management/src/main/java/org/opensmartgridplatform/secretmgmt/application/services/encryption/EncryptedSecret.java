package org.opensmartgridplatform.secretmgmt.application.services.encryption;

import lombok.Getter;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;

/**
 * Secret class to store any binary encrypted secret. The secret can be anything.
 * A secret contains the type of encryption provider that was used to encrypt it.
 * There is no encoding/decoding.
 */
@Getter
public class EncryptedSecret {
    private final byte[] secret;
    private final EncryptionProviderType type;

    public EncryptedSecret(final EncryptionProviderType type, final byte[] secret) {
        this.type = type;
        this.secret = secret;
    }
}
