/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.shared.security;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

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

    public static byte[] decrypt(final byte[] inputData, final String devicePrivateKeyPath) throws TechnicalException {
        byte[] decryptedData = null;
        PrivateKey privateKey;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(devicePrivateKeyPath))) {
            // Read the private key from the file.
            privateKey = (PrivateKey) inputStream.readObject();

            // Get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(algorithm);

            // Decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedData = cipher.doFinal(inputData);
        } catch (final Exception ex) {
            LOGGER.error("Unexpected exception during decryption", ex);
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "Error while decrypting RSA key!");
        }
        return decryptedData;
    }

    public static byte[] encrypt(final byte[] inputData, final String publicKeyPath) throws TechnicalException {
        byte[] cipherData = null;
        PublicKey publicKey;

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(publicKeyPath))) {
            // Read the public key from the file.
            publicKey = (PublicKey) inputStream.readObject();

            // Get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(algorithm);

            // Encrypt the data using the public key
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipherData = cipher.doFinal(inputData);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during encryption", e);
            throw new TechnicalException(ComponentType.WS_SMART_METERING, "Error while encrypting RSA key!");
        }
        return cipherData;
    }
}
