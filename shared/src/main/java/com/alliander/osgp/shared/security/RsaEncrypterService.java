/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.security;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.exceptionhandling.RsaEncrypterException;

/**
 * RSA Encryption service class that offers encrypt and decrypt methods to
 * encrypt or decrypt data.
 *
 * Both methods accept a file location path, which should lead to the location
 * of the private or public key.
 */
public final class RsaEncrypterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsaEncrypterService.class);
    private static final String ALGORITHM = "RSA";

    private RsaEncrypterService() {
        /*
         * Private Constructor will prevent the instantiation of this class
         * directly
         */
    }

    /**
     * Decrypts the data using the private key
     */
    public static byte[] decrypt(final byte[] inputData, final String devicePrivateKeyPath) {
        final PrivateKey privateKey = getPrivateKey(devicePrivateKeyPath);

        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(inputData);
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException ex) {
            LOGGER.error("Unexpected exception during decryption", ex);
            throw new RsaEncrypterException("Error while decrypting RSA key!", ex);
        }
    }

    /**
     * Encrypts the data using the public key
     */
    public static byte[] encrypt(final byte[] inputData, final String devicePublicKeyPath) {
        final PublicKey publicKey = getPublicKey(devicePublicKeyPath);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(inputData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            LOGGER.error("Unexpected exception during encryption", e);
            throw new RsaEncrypterException("Error while encrypting RSA key!", e);
        }
    }

    /**
     * Fetches the private key file (DER format).
     *
     * @param filename
     * @return PrivateKey
     */
    private static PrivateKey getPrivateKey(final String filename) {
        final File file = new File(filename);
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            final byte[] keyBytes = new byte[(int) file.length()];
            dis.readFully(keyBytes);

            final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            final KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.error("Unexpected exception while reading private key", e);
            throw new RsaEncrypterException("Unexpected exception while reading private key", e);
        }
    }

    /**
     * Fetches the public key file (DER format).
     *
     * @param filename
     * @return PublicKey
     */
    private static PublicKey getPublicKey(final String filename) {
        final File file = new File(filename);
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            final byte[] keyBytes = new byte[(int) file.length()];
            dis.readFully(keyBytes);

            final X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            final KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.error("Unexpected exception while reading public key", e);
            throw new RsaEncrypterException("Unexpected exception while reading public key", e);
        }
    }
}
