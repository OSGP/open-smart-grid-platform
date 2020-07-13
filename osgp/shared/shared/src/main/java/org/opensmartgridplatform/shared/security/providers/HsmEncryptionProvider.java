/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
    package org.opensmartgridplatform.shared.security.providers;

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
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Hex;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.Secret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HsmEncryptionProvider extends AbstractEncryptionProvider implements EncryptionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(HsmEncryptionProvider.class);

    private static final int KEY_LENGTH = 16;
    private static final String ALGORITHM = "AES/CBC/NoPadding";
    private static final String PROVIDER = "nCipherKM";
    private static final String TYPE = "ncipher.sworld";
    private static final byte[] IV = new byte[KEY_LENGTH];

    private final KeyStore keyStore;

    public HsmEncryptionProvider(File keyStoreFile) {
        try {
            super.setKeyFile(keyStoreFile);
            this.keyStore = KeyStore.getInstance(TYPE, PROVIDER);
            FileInputStream fIn = new FileInputStream(keyStoreFile);
            this.keyStore.load(fIn, null);
        } catch (CertificateException | NoSuchAlgorithmException | NoSuchProviderException | IOException | KeyStoreException e) {
            throw new EncrypterException("Could not read keystore", e);
        }
    }

    public Secret decrypt(EncryptedSecret secret, String keyReference) {

        Secret decryptedSecret = super.decrypt(secret, keyReference);

        byte[] decryptedSecretBytes = decryptedSecret.getSecret();

        if (decryptedSecretBytes.length > KEY_LENGTH) {

            byte[] truncatedDecryptedSecretBytes = Arrays.copyOfRange(decryptedSecretBytes,
                    decryptedSecretBytes.length-16, decryptedSecretBytes.length);

            LOGGER.trace("Truncating decrypted key from " + Hex.encodeHexString(decryptedSecretBytes) + " to " +
                            Hex.encodeHexString(truncatedDecryptedSecretBytes));

            decryptedSecret = new Secret(truncatedDecryptedSecretBytes);
        }

        return decryptedSecret;
    }

    protected Cipher getCipher() throws EncrypterException {
        try {
            return Cipher.getInstance(ALGORITHM, PROVIDER);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new EncrypterException("Could not get cipher", e);
        }
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
            throw new EncrypterException("Could not get keystore from key", e);
        }
    }

    protected AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return new IvParameterSpec(IV);
    }

    public EncryptionProviderType getType() {
        return EncryptionProviderType.HSM;
    }
}