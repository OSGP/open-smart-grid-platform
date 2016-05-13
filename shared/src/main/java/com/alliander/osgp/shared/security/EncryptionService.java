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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.Arrays;
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

    private final SecretKey key;
    private static final byte[] IVBYTES = new byte[16];

    static {
        for (short s = 0; s < IVBYTES.length; s++) {
            IVBYTES[s] = (byte) s;
        }
    }

    public EncryptionService(final String keyPath) {
        try {
            this.key = new SecretKeySpec(Files.readAllBytes(new File(keyPath).toPath()), "AES");
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
            cipher.init(Cipher.DECRYPT_MODE, this.key, new IvParameterSpec(IVBYTES));
            return cipher.doFinal(inputData);
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException
                | InvalidAlgorithmParameterException ex) {
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
            cipher.init(Cipher.ENCRYPT_MODE, this.key, new IvParameterSpec(IVBYTES));
            return cipher.doFinal(inputData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            LOGGER.error("Unexpected exception during encryption", e);
            throw new EncrypterException("Unexpected exception during encryption!", e);
        }
    }

    public static byte[] getIvbytes() {
        return Arrays.clone(IVBYTES);
    }

}
