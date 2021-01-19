/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security.providers;

import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;

public interface EncryptionProvider {

    EncryptedSecret encrypt(byte[] secret, String keyReference);
    byte[] decrypt(EncryptedSecret secret, String keyReference);

    /**
     * Generate an AES-encrypted 128-bits secret
     * @param keyReference the reference to the key used for AES-encrypting the secret
     * @return the AES-encrypted secret
     * @throws EncrypterException when the key cannot be generated (with a specified algoritm) or when AES-encryption
     * goes wrong
     */
    byte[] generateAes128BitsSecret(String keyReference);
    int getSecretByteLength();
    EncryptionProviderType getType();
}

