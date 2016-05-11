/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.exceptionhandling.EncrypterException;

/**
 * Encryption service class that offers encrypt and decrypt methods to encrypt
 * or decrypt data.
 *
 * Both methods accept a file location path, which should lead to the location
 * of the secret key.
 */
public final class EncryptionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionService.class);
    /**
     * the algorithm used
     */
    public static final String ALGORITHM = "AES/CBC/PKCS7PADDING";
    /**
     * the id of the provider used
     */
    public static final String PROVIDER = "BC";

    public EncryptionService(final String keyPath) {
        try {
            this.cachedKey = new SecretKeySpec(Files.readAllBytes(new File(keyPath).toPath()), "AES");
        } catch (final IOException e) {
            LOGGER.error("Unexpected exception when reading key", e);
            throw new EncrypterException("Unexpected exception when reading key", e);
        }
    }

    /**
     * Decrypts the data using the key
     */
    public byte[] decrypt(final byte[] inputData) {

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, this.cachedKey);
            return cipher.doFinal(inputData);
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException ex) {
            LOGGER.error("Unexpected exception during decryption", ex);
            throw new EncrypterException("Unexpected exception during decryption!", ex);
        }
    }

    /**
     * Encrypts the data using the key
     */
    public byte[] encrypt(final byte[] inputData) {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, this.cachedKey);
            return cipher.doFinal(inputData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | NoSuchProviderException e) {
            LOGGER.error("Unexpected exception during encryption", e);
            throw new EncrypterException("Unexpected exception during encryption!", e);
        }
    }

    private final SecretKey cachedKey;

}
