/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services.encryption.providers;

import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptionProviderType;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;

public class HsmEncryptionProvider extends AbstractEncryptionProvider implements EncryptionProvider {

    private static final String ALGORITHM = "AES/CBC/NoPadding";
    private static final String PROVIDER = "nCipherKM";
    private static final String TYPE = "ncipher.sworld";
    private static final byte[] IV = new byte[16];

    private KeyStore keyStore;

    public HsmEncryptionProvider(File keyStoreFile) {
        try {
            super.setKeyFile(keyStoreFile);
            this.keyStore = KeyStore.getInstance(TYPE, PROVIDER);
            FileInputStream fIn = new FileInputStream(keyStoreFile);
            this.keyStore.load(fIn, null);
        } catch (CertificateException | NoSuchAlgorithmException | NoSuchProviderException | IOException | KeyStoreException e) {
            throw new IllegalStateException("Could not read keystore");
        }
    }

    protected Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        return Cipher.getInstance(ALGORITHM, PROVIDER);
    }

    /**
     * This method reads the 'actual' encryption key (from the database).
     * Normally this is the key start isValidFrom(now) and isValidUntil(now).
     *
     * @return the key that must be used for encryption/decryption
     */
    protected Key getSecretEncryptionKey(String keyReference, int cipherMode) {
        try {
            return this.keyStore.getKey(keyReference, null);
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new IllegalStateException("Could not get keystore from key", e);
        }
    }

    protected AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return new IvParameterSpec(IV);
    }

    public EncryptionProviderType getType() {
        return EncryptionProviderType.HSM;
    }
}