/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.oslp;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;

/**
 * Utility methods to ease usage of OSLP.
 */
public class OslpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpUtils.class);

    /**
     * Fallback signature value which triggers previous RSA/Hash implementation.
     */
    public static final String FALLBACK_SIGNATURE = "SHA512encryptedwithRSA";

    /**
     * Fallback RSA / Padding settings for Cipher
     */
    public static final String FALLBACK_CIPHER = "RSA/ECB/PKCS1Padding";

    /**
     * Fallback digest to create hash from previous RSA/Hash implementation.
     */
    public static final String FALLBACK_DIGEST = "SHA-512";

    public OslpUtils() {
        // Public constructor.
    }

    /**
     * Converts an {@link Integer} to a {@link ByteString}.
     *
     * @param i
     *            the {@link Integer} to convert.
     * @return the {@link ByteString}.
     * @throws IllegalArgumentException
     *             thrown when i is null.
     */
    public static ByteString integerToByteString(final Integer i) {
        if (i == null) {
            throw new IllegalArgumentException("Null cannot be converted to ByteString.");
        }

        return ByteString.copyFrom(new byte[] { i.byteValue() });
    }

    /**
     * Convert byteString to Integer
     *
     * @param b
     *            bytestring input
     * @return converted integer
     */
    public static Integer byteStringToInteger(final ByteString b) {
        if (b == null) {
            throw new IllegalArgumentException("Null cannot be converted to Integer.");
        }

        if (b.isEmpty()) {
            return null;
        }

        return (int) b.byteAt(0);
    }

    /**
     * Combine all bytes which need to be signed from the OSLP envelope.
     *
     * @param envelope
     * @return array of bytes which can be signed
     */
    public static byte[] createSignBytes(final OslpEnvelope envelope) {
        byte[] message = ArrayUtils.addAll(envelope.getSequenceNumber(), envelope.getDeviceId());
        message = ArrayUtils.addAll(message, envelope.getLengthIndicator());
        message = ArrayUtils.addAll(message, envelope.getPayloadMessage().toByteArray());

        return message;
    }

    /**
     * Create a signature of specified message.
     *
     * @param message
     *            message bytes to sign
     * @param privateKey
     *            private key to use for signing
     * @param signature
     *            signature algorithm to use
     * @param provider
     *            provider which supplies the signature algorithm
     * @return signature
     * @throws GeneralSecurityException
     *             when configuration is incorrect.
     */
    public static byte[] createSignature(final byte[] message, final PrivateKey privateKey, final String signature,
            final String provider) throws GeneralSecurityException {
        // Use fallback to plain SHA512 hash, which is encrypted with RSA
        // instead of real RSA signature
        if (signature.equalsIgnoreCase(FALLBACK_SIGNATURE)) {
            return createEncryptedHash(message, privateKey);
        }

        // Use real signature
        final Signature signatureBuilder = Signature.getInstance(signature, provider);
        signatureBuilder.initSign(privateKey);
        signatureBuilder.update(message);
        return signatureBuilder.sign();
    }

    /**
     * Validate the signature against the message.
     *
     * @param message
     *            message to validate
     * @param securityKey
     *            signature to validate
     * @param publicKey
     *            public key to use for decryption of signature
     * @param signature
     *            signature algorithm to use
     * @param provider
     *            provider which supplies algorithm
     * @return true when signature is correct, false when it's not
     * @throws GeneralSecurityException
     *             when configuration is incorrect.
     */
    public static boolean validateSignature(final byte[] message, final byte[] securityKey, final PublicKey publicKey,
            final String signature, final String provider) throws GeneralSecurityException {

        // Use fallback to plain SHA512 hash, which is encrypted with RSA
        // instead of real RSA signature
        if (signature.equalsIgnoreCase(FALLBACK_SIGNATURE)) {
            return validateEncryptedHash(message, securityKey, publicKey);
        }

        final Signature signatureBuilder = Signature.getInstance(signature, provider);
        signatureBuilder.initVerify(publicKey);
        signatureBuilder.update(message);

        return signatureBuilder.verify(securityKey);
    }

    private static byte[] createEncryptedHash(final byte[] message, final PrivateKey privateKey)
            throws GeneralSecurityException {

        final byte[] hash = createHash(message);

        // Encrypt the hash
        final Cipher cipher = Cipher.getInstance(FALLBACK_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(hash);
    }

    private static byte[] createHash(final byte[] message) throws GeneralSecurityException {
        // Create digest Hash
        final MessageDigest digest = MessageDigest.getInstance(FALLBACK_DIGEST);
        return digest.digest(message);
    }

    private static boolean validateEncryptedHash(final byte[] message, final byte[] securityKey,
            final PublicKey publicKey) throws GeneralSecurityException {

        // Calculate hash of message
        final byte[] verifyHash = createHash(message);

        try {
            // Decrypt security key hash
            final Cipher cipher = Cipher.getInstance(FALLBACK_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            final byte[] messageHash = cipher.doFinal(securityKey);

            // Verify calculated and received hash
            return Arrays.equals(messageHash, verifyHash);
        } catch (final BadPaddingException e) {
            LOGGER.error("unexpected exception", e);
            return false;
        }
    }
}
