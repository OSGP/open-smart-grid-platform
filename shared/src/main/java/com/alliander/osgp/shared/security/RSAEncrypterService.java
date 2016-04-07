/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.shared.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

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
    private static final String algorithm = "RSA";

    private RSAEncrypterService() {
        /*
         * Private Constructor will prevent the instantiation of this class
         * directly
         */
    }

    /**
     * Reads the private key from the file and decrypts the data using the
     * private key
     */
    public static byte[] decrypt(final byte[] inputData, final String devicePrivateKeyPath)
            throws RsaEncrypterException {
        byte[] decryptedData = null;
        PrivateKey privateKey;

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(devicePrivateKeyPath))) {
            privateKey = (PrivateKey) inputStream.readObject();
            final Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedData = cipher.doFinal(inputData);
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException | ClassNotFoundException | IOException
                | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            LOGGER.error("Unexpected exception during decryption", ex);
            throw new RsaEncrypterException("Error while decrypting RSA key!", ex);
        }
        return decryptedData;
    }

    /**
     * Reads the public key from the file and encrypts the data using the public
     * key
     */
    public static byte[] encrypt(final byte[] inputData, final String publicKeyPath) throws RsaEncrypterException {
        byte[] cipherData = null;
        PublicKey publicKey;

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(publicKeyPath))) {
            publicKey = (PublicKey) inputStream.readObject();
            final Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipherData = cipher.doFinal(inputData);
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException | ClassNotFoundException | IOException
                | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            LOGGER.error("Unexpected exception during encryption", ex);
            throw new RsaEncrypterException("Error while encrypting RSA key!", ex);
        }
        return cipherData;
    }
}
