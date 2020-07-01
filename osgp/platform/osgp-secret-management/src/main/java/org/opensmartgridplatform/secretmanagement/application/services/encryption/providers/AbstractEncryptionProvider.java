/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services.encryption.providers;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptionProviderType;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.Secret;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import java.io.File;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

@Slf4j
public abstract class AbstractEncryptionProvider {

    private static final int BLOCK_SIZE=16;

    protected File keyFile;

    public abstract EncryptionProviderType getType();

    protected abstract Cipher getCipher()
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException;

    protected abstract AlgorithmParameterSpec getAlgorithmParameterSpec();

    protected abstract Key getSecretEncryptionKey(String keyReference, int cipherMode);

    public void setKeyFile(File keyFile) {
        this.keyFile = keyFile;
    }

    public EncryptedSecret encrypt(Secret secret, String keyReference) {
        try {
            final Cipher cipher = this.getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, this.getSecretEncryptionKey(keyReference, Cipher.ENCRYPT_MODE),
                    this.getAlgorithmParameterSpec());
            return new EncryptedSecret(this.getType(), cipher.doFinal(secret.getSecret()));
        } catch (Exception e) {
            //InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
            // InvalidAlgorithmParameterException |
            //NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException
            throw new IllegalStateException("Could not encrypt secret with keyReference " + keyReference, e);
        }
    }

    public Secret decrypt(EncryptedSecret secret, String keyReference) {

        if (secret.getType() != this.getType()) {
            throw new IllegalStateException(
                    "EncryptionProvider for type " + this.getType().name() + " cannot decrypt secrets of type "
                            + secret.getType().name());
        }

        try {
            final Cipher cipher = this.getCipher();
            cipher.init(Cipher.DECRYPT_MODE, this.getSecretEncryptionKey(keyReference, Cipher.DECRYPT_MODE),
                    this.getAlgorithmParameterSpec());
            final byte[] decryptedData = cipher.doFinal(secret.getSecret());

            if (this.checkNullBytesPrepended(decryptedData)) {
                return new Secret(Arrays.copyOfRange(decryptedData, BLOCK_SIZE, decryptedData.length));
            } else {
                return new Secret(decryptedData);
            }
        } catch (Exception e) {
            //InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
            // InvalidAlgorithmParameterException |
            //NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException
            throw new IllegalStateException("Could not decrypt secret with keyReference " + keyReference, e);
        }
    }

    private boolean checkNullBytesPrepended(final byte[] bytes) {
        if (bytes.length > BLOCK_SIZE) {
            for (short s = 0; s < BLOCK_SIZE; s++) {
                if (bytes[s] != 0) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
