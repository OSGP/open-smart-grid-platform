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
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;

public class JreEncryptionProvider extends AbstractEncryptionProvider implements EncryptionProvider {

    public static final String DEFAULT_SINGLE_KEY_REFERENCE = "1";
    public static final String ALG = "AES";
    public static final String ALGORITHM = "AES/CBC/PKCS5PADDING";
    public static final String PROVIDER = "SunJCE";
    public static final String FORMAT = "RAW";
    private static final byte[] IV = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

    private byte[] key;

    protected int getIVLength() {
        return IV.length;
    }

    protected Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        return Cipher.getInstance(ALGORITHM, PROVIDER);
    }

    public JreEncryptionProvider(File keyStoreFile) {
        try {
            super.setKeyFile(keyStoreFile);
            this.key = Files.readAllBytes(Paths.get(keyStoreFile.getAbsolutePath()));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read keystore");
        }
    }

    protected Key getSecretEncryptionKey(String keyReference) {

        if (!keyReference.equals(DEFAULT_SINGLE_KEY_REFERENCE)) {
            throw new IllegalStateException("Only keyReference '1' is valid in this implementation.");
        }

        return new SecretKey() {
            @Override
            public String getAlgorithm() {
                return ALG;
            }

            @Override
            public String getFormat() {
                return FORMAT;
            }

            @Override
            public byte[] getEncoded() {
                return key;
            }
        };
    }

    protected AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return new IvParameterSpec(IV);
    }

    public EncryptionProviderType getType() {
        return EncryptionProviderType.JRE;
    }
}
