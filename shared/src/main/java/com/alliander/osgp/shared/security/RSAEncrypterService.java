/**
 * Copyright 2016 Smart Society Services B.V.
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
public final class RSAEncrypterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RSAEncrypterService.class);
    private static final String ALGORITHM = "RSA";

    private RSAEncrypterService() {
        /*
         * Private Constructor will prevent the instantiation of this class
         * directly
         */
    }

    /**
     * Decrypts the data using the private key
     */
    public static byte[] decrypt(final byte[] inputData, String devicePrivateKeyPath) {
        devicePrivateKeyPath = "/home/dev/Sources/Configuration/developers/certs/devicekey_priv.der";
        byte[] decryptedData = null;
        final PrivateKey privateKey = getPrivateKey(devicePrivateKeyPath);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedData = cipher.doFinal(inputData);
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException ex) {
            LOGGER.error("Unexpected exception during decryption", ex);
            throw new RsaEncrypterException("Error while decrypting RSA key!", ex);
        }
        return decryptedData;
    }

    /**
     * Encrypts the data using the public key
     */
    public static byte[] encrypt(final byte[] inputData, String devicePublicKeyPath) {
        devicePublicKeyPath = "/home/dev/Sources/Configuration/developers/certs/devicekey_pub.der";
        byte[] encryptedData = null;
        final PublicKey publicKey = getPublicKey(devicePublicKeyPath);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encryptedData = cipher.doFinal(inputData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            LOGGER.error("Unexpected exception during encryption", e);
            throw new RsaEncrypterException("Error while encrypting RSA key!", e);
        }
        return encryptedData;
    }

    public static PrivateKey getPrivateKey(final String filename) {
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

    public static PublicKey getPublicKey(final String filename) {
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
