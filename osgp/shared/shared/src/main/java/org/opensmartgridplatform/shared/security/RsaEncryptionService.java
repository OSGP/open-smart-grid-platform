/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

/**
 * {@link RsaEncryptionService} provides methods for encrypting and/or
 * decrypting based on {@value #ALGORITHM} {@link RSAPublicKey public} and
 * {@link RSAPrivateKey private} keys. It uses a cipher with transformation
 * {@value #TRANSFORMATION}.
 * <p>
 * The service is only able to {@link #encrypt(byte[]) encrypt bytes} if a
 * public key is configured for property key
 * {@code encryption.rsa.public.key.path}, which should be a path to a readable
 * file.<br>
 * This public key file should contain binary data according to the
 * {@link X509EncodedKeySpec X.509 encoded key spec}.
 * <p>
 * The service is only able to {@link #decrypt(byte[]) decrypt bytes} if a
 * private key is configured for property key
 * {@code encryption.rsa.private.key.path}, which should be a path to a readable
 * file.<br>
 * This private key file should contain binary data according to the
 * {@link PKCS8EncodedKeySpec PKCS#8 encoded key spec}.
 */
@Component
public class RsaEncryptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RsaEncryptionService.class);

    public static final String ALGORITHM = "RSA";
    public static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private static final String UNEXPECTED_EXCEPTION_INITIALIZING_ENCRYPTION =
            "Unexpected exception initializing " + "encryption";
    private static final String UNEXPECTED_EXCEPTION_GENERATING_KEY_PAIR =
            "Unexpected exception generating a new key" + " pair";
    private static final String UNEXPECTED_EXCEPTION_WHEN_READING_PRIVATE_KEY =
            "Unexpected exception when reading " + "private key";
    private static final String UNEXPECTED_EXCEPTION_WHEN_READING_PUBLIC_KEY =
            "Unexpected exception when reading " + "public key";
    private static final String PUBLIC_KEY_IS_NOT_AN_RSA_KEY = "Public key is not an RSA key";
    private static final String PRIVATE_KEY_IS_NOT_AN_RSA_KEY = "Private key is not an RSA key";
    private static final String PUBLIC_KEY_DOES_NOT_BELONG_WITH_PRIVATE_KEY =
            "Public key does not belong with " + "private key";
    private static final String UNEXPECTED_EXCEPTION_DURING_ENCRYPTION = "Unexpected exception during encryption";
    private static final String UNEXPECTED_EXCEPTION_DURING_DECRYPTION = "Unexpected exception during decryption";
    private static final String PUBLIC_KEY_MUST_BE_CONFIGURED_FOR_ENCRYPTION =
            "Public key must be configured for " + "encryption";
    private static final String PRIVATE_KEY_MUST_BE_CONFIGURED_FOR_DECRYPTION =
            "Private key must be configured for " + "decryption";

    @Value("${encryption.rsa.private.key.path:#{null}}")
    private Resource rsaPrivateKeyPath;
    @Value("${encryption.rsa.public.key.path:#{null}}")
    private Resource rsaPublicKeyPath;

    private KeyPair keyPair;

    public RsaEncryptionService() {
        // Default constructor.
    }

    /**
     * Constructor for easier testability.
     * <p>
     * If the public key in the keyPair is {@code null}, encryption will not be
     * available.<br>
     * If the private key in the keyPair is {@code null}, decryption will not be
     * available.
     *
     * @param keyPair
     *         a key pair for algorithm {@value #ALGORITHM}.
     */
    protected RsaEncryptionService(final KeyPair keyPair) {
        /*
         * Call createKeyPair to check if the private and public key belong with
         * each other.
         */
        this.keyPair = this.createKeyPair(keyPair.getPublic(), keyPair.getPrivate());
    }

    public RsaEncryptionService(final Resource rsaPublicKeyPath, final Resource rsaPrivateKeyPath) {

        final PrivateKey privateKey = readPrivateKeyFromResource(rsaPrivateKeyPath);
        final PublicKey publicKey = readPublicKeyFromResource(rsaPublicKeyPath);

        this.keyPair = this.createKeyPair(publicKey, privateKey);
    }

    @PostConstruct
    private void initEncryption() {
        this.keyPair = this.createKeyPair(this.rsaPublicKeyPath, this.rsaPrivateKeyPath);
    }

    /**
     * Note that the {@link RsaEncryptionService} may be used in a place where
     * the public key of the key pair in use is not configured. In such a case a
     * call to this method will result in an exception.
     *
     * @param input
     *         bytes to be encrypted.
     *
     * @return bytes encrypted with the public key of the configured key pair.
     *
     * @throws EncrypterException
     *         if the public key is not configured or if anything goes wrong
     *         while encrypting the given {@code input}.
     */
    public byte[] encrypt(final byte[] input) {
        try {
            final Cipher cipher = this.getCipherForEncryption();
            return cipher.doFinal(input);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.error(UNEXPECTED_EXCEPTION_DURING_ENCRYPTION, e);
            throw new EncrypterException(UNEXPECTED_EXCEPTION_DURING_ENCRYPTION, e);
        }
    }

    /**
     * Note that the {@link RsaEncryptionService} may be used in a place where
     * the private key of the configured key pair is not available. In such a
     * case a call to this method will result in an exception.
     *
     * @param input
     *         bytes encrypted with the public key of the configured key
     *         pair.
     *
     * @return bytes decrypted with the private key of the configured key pair.
     *
     * @throws EncrypterException
     *         if the private key is not configured or if anything goes
     *         wrong while decrypting the given {@code input}.
     */
    public byte[] decrypt(final byte[] input) {
        try {
            final Cipher cipher = this.getCipherForDecryption();
            return cipher.doFinal(input);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.error(UNEXPECTED_EXCEPTION_DURING_DECRYPTION, e);
            throw new EncrypterException(UNEXPECTED_EXCEPTION_DURING_DECRYPTION, e);
        }
    }

    private Cipher getCipherForEncryption()
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        final PublicKey publicKey = this.keyPair.getPublic();
        if (publicKey == null) {
            LOGGER.error(PUBLIC_KEY_MUST_BE_CONFIGURED_FOR_ENCRYPTION);
            throw new EncrypterException(PUBLIC_KEY_MUST_BE_CONFIGURED_FOR_ENCRYPTION);
        }
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher;
    }

    private Cipher getCipherForDecryption()
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        final PrivateKey privateKey = this.keyPair.getPrivate();
        if (privateKey == null) {
            LOGGER.error(PRIVATE_KEY_MUST_BE_CONFIGURED_FOR_DECRYPTION);
            throw new EncrypterException(PRIVATE_KEY_MUST_BE_CONFIGURED_FOR_DECRYPTION);
        }
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher;
    }

    private KeyPair createKeyPair(final Resource rsaPublicKeyPath, final Resource rsaPrivateKeyPath) {
        final PrivateKey privateKey = readPrivateKeyFromResource(rsaPrivateKeyPath);
        final PublicKey publicKey = readPublicKeyFromResource(rsaPublicKeyPath);
        return this.createKeyPair(publicKey, privateKey);
    }

    private KeyPair createKeyPair(final PublicKey publicKey, final PrivateKey privateKey) {
        if (publicKey == null || privateKey == null) {
            /*
             * Public and private key are not both available, no need to check
             * if they are a pair belonging together.
             */
            return new KeyPair(publicKey, privateKey);
        }
        final RSAPublicKey rsaPublicKey = this.getRsaPublicKey(publicKey);
        final RSAPrivateKey rsaPrivateKey = this.getRsaPrivateKey(privateKey);
        /*
         * Check if the mathematical relation between the RSA key fields for a
         * key pair that belongs together applies to the given keys.
         */
        final BigInteger modulus = rsaPublicKey.getModulus();
        final BigInteger two = BigInteger.valueOf(2);
        final BigInteger publicExponent = rsaPublicKey.getPublicExponent();
        final BigInteger privateExponent = rsaPrivateKey.getPrivateExponent();
        if (modulus.equals(rsaPrivateKey.getModulus()) && two
                .modPow(publicExponent.multiply(privateExponent).subtract(BigInteger.ONE), modulus)
                .equals(BigInteger.ONE)) {
            return new KeyPair(publicKey, privateKey);
        }
        LOGGER.error(PUBLIC_KEY_DOES_NOT_BELONG_WITH_PRIVATE_KEY);
        throw new EncrypterException(PUBLIC_KEY_DOES_NOT_BELONG_WITH_PRIVATE_KEY);
    }

    private RSAPublicKey getRsaPublicKey(final PublicKey publicKey) {
        try {
            return (RSAPublicKey) publicKey;
        } catch (final ClassCastException e) {
            LOGGER.error(PUBLIC_KEY_IS_NOT_AN_RSA_KEY, e);
            throw new EncrypterException(PUBLIC_KEY_IS_NOT_AN_RSA_KEY, e);
        }
    }

    private RSAPrivateKey getRsaPrivateKey(final PrivateKey privateKey) {
        try {
            return (RSAPrivateKey) privateKey;
        } catch (final ClassCastException e) {
            LOGGER.error(PRIVATE_KEY_IS_NOT_AN_RSA_KEY, e);
            throw new EncrypterException(PRIVATE_KEY_IS_NOT_AN_RSA_KEY, e);
        }
    }

    /**
     * Convenience method to read a public key from a file with the given
     * {@code filename}.
     * <p>
     * If this method returns a usable key for a certain file, the
     * {@link RsaEncryptionService} should be able to use that key for
     * {@link #encrypt(byte[]) encryption} if the filename is configured as
     * property key {@code encryption.rsa.public.key.path}.
     *
     * @param filename
     *         the name of a file containing binary data according to the
     *         {@link X509EncodedKeySpec X.509 encoded key spec}.
     *
     * @return a public key for algorithm {@value #ALGORITHM}, or {@code null}
     *         if the given {@code filename} is blank.
     */
    public static PublicKey readPublicKeyFromFile(final String filename) {
        if (StringUtils.isBlank(filename)) {
            return null;
        }
        try {
            final byte[] publicKeyBytes = Files.readAllBytes(Paths.get(filename));
            return generatePublicKey(publicKeyBytes);
        } catch (final IOException | InvalidKeySpecException e) {
            LOGGER.error(UNEXPECTED_EXCEPTION_WHEN_READING_PUBLIC_KEY, e);
            throw new EncrypterException(UNEXPECTED_EXCEPTION_WHEN_READING_PUBLIC_KEY, e);
        }
    }

    private static PublicKey generatePublicKey(byte[] publicKeyBytes) throws InvalidKeySpecException {
        final X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return getKeyFactory().generatePublic(publicKeySpec);
    }

    /**
     * Convenience method to read a private key from a file with the given
     * {@code filename}.
     * <p>
     * If this method returns a usable key for a certain file, the
     * {@link RsaEncryptionService} should be able to use that key for
     * {@link #decrypt(byte[]) decryption} if the filename is configured as
     * property key {@code encryption.rsa.private.key.path}.
     *
     * @param filename
     *         the name of a file containing binary data according to the
     *         {@link PKCS8EncodedKeySpec PKCS#8 encoded key spec}.
     *
     * @return a public key for algorithm {@value #ALGORITHM}, or {@code null}
     *         if the given {@code filename} is blank.
     */
    public static PrivateKey readPrivateKeyFromFile(final String filename) {
        if (StringUtils.isBlank(filename)) {
            return null;
        }
        try {
            final byte[] privateKeyBytes = Files.readAllBytes(Paths.get(filename));
            return generatePrivateKey(privateKeyBytes);
        } catch (final IOException | InvalidKeySpecException e) {
            LOGGER.error(UNEXPECTED_EXCEPTION_WHEN_READING_PRIVATE_KEY, e);
            throw new EncrypterException(UNEXPECTED_EXCEPTION_WHEN_READING_PRIVATE_KEY, e);
        }
    }

    private static PrivateKey readPrivateKeyFromResource(final Resource resource) {

        if (resource == null) {
            return null;
        }

        try {
            final byte[] privateKeyBytes = StreamUtils.copyToByteArray(resource.getInputStream());
            return generatePrivateKey(privateKeyBytes);
        } catch (final IOException | InvalidKeySpecException e) {
            LOGGER.error(UNEXPECTED_EXCEPTION_WHEN_READING_PRIVATE_KEY, e);
            throw new EncrypterException(UNEXPECTED_EXCEPTION_WHEN_READING_PRIVATE_KEY, e);
        }
    }

    private static PrivateKey generatePrivateKey(byte[] privateKeyBytes) throws InvalidKeySpecException {
        final PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return getKeyFactory().generatePrivate(privateKeySpec);
    }

    private static PublicKey readPublicKeyFromResource(final Resource resource) {

        if (resource == null) {
            return null;
        }

        try {
            final byte[] publicKeyBytes = StreamUtils.copyToByteArray(resource.getInputStream());
            return generatePublicKey(publicKeyBytes);
        } catch (final IOException | InvalidKeySpecException e) {
            LOGGER.error(UNEXPECTED_EXCEPTION_WHEN_READING_PUBLIC_KEY, e);
            throw new EncrypterException(UNEXPECTED_EXCEPTION_WHEN_READING_PUBLIC_KEY, e);
        }

    }

    private static KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance(ALGORITHM);
        } catch (final NoSuchAlgorithmException e) {
            LOGGER.error(UNEXPECTED_EXCEPTION_INITIALIZING_ENCRYPTION, e);
            throw new EncrypterException(UNEXPECTED_EXCEPTION_INITIALIZING_ENCRYPTION, e);
        }
    }

    /**
     * Convenience method to create a new keypair for algorithm
     * {@value #ALGORITHM} and the given {@code keysize}.
     *
     * @param keysize
     *         the keysize for which the key pair generator will be
     *         initialized.
     *
     * @return a randomly initialized {@value #ALGORITHM} key pair for the given
     *         {@code keysize}.
     */
    public static KeyPair createKeyPair(final int keysize) {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(keysize);
            return keyPairGenerator.generateKeyPair();
        } catch (final NoSuchAlgorithmException e) {
            LOGGER.error(UNEXPECTED_EXCEPTION_GENERATING_KEY_PAIR, e);
            throw new EncrypterException(UNEXPECTED_EXCEPTION_GENERATING_KEY_PAIR, e);
        }
    }

    /**
     * Convenience method to store a key to a file with the given
     * {@code filename}.
     * <p>
     * If the given key is for the correct algorithm, the file should be usable
     * as input for reading a {@link #readPublicKeyFromFile(String) public} or
     * {@link #readPrivateKeyFromFile(String) private} key.
     *
     * @param key
     *         a key implementation for algorithm {@value #ALGORITHM}.
     * @param filename
     *         the name of a file to be newly created.
     *
     * @throws IOException
     *         if a file with the given {@code filename} already exists or
     *         cannot be created or written to.
     */
    public static void saveKeyToFile(final Key key, final String filename) throws IOException {
        final byte[] encoded = key.getEncoded();
        final Path path = Paths.get(filename);
        Files.write(path, encoded, StandardOpenOption.CREATE_NEW);
    }
}
