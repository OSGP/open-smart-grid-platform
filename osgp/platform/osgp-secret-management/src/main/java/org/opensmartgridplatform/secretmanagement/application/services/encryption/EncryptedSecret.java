/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services.encryption;

import lombok.Getter;

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
