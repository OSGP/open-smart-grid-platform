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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;

public class JreEncryptionProvider extends AbstractEncryptionProvider implements EncryptionProvider {

    private static final String DEFAULT_SINGLE_KEY_REFERENCE = "1";
    private static final String ALG = "AES";
    private static final String ALGORITHM = "AES/CBC/PKCS5PADDING";
    private static final String PROVIDER = "SunJCE";
    private static final String FORMAT = "RAW";
    private static final byte[] IV = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

    private final byte[] key;

    public JreEncryptionProvider(File keyStoreFile) {
        try {
            super.setKeyFile(keyStoreFile);
            this.key = Files.readAllBytes(Paths.get(keyStoreFile.getAbsolutePath()));
        } catch (IOException e) {
            throw new EncrypterException("Could not read keystore", e);
        }
    }

    protected Cipher getCipher() {
        try {
            return Cipher.getInstance(ALGORITHM, PROVIDER);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new EncrypterException("Could not get cipher", e);
        }
    }

    protected Key getSecretEncryptionKey(String keyReference, int cipherMode) {

        if (!keyReference.equals(DEFAULT_SINGLE_KEY_REFERENCE)) {
            throw new EncrypterException("Only keyReference '1' is valid in this implementation.");
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
