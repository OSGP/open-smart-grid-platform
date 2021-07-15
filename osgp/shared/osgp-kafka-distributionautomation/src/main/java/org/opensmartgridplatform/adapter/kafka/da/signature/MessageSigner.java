/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.signature;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import com.alliander.data.scadameasurementpublishedevent.Message;

/**
 * MessageSigner can be used for signing {@link Message messages} or verifying
 * {@link Message message} {@link Message#signature signatures}.
 * <p>
 * Using the defaults a {@value #DEFAULT_SIGNATURE_KEY_SIZE}-bits
 * {@value #DEFAULT_SIGNATURE_KEY_ALGORITHM} encrypted
 * {@link #DEFAULT_SIGNATURE_ALGORITHM SHA-256} hash over the message bytes
 * (with its {@link Message#signature signature} set to {@code null}) is created
 * or verified.
 * <p>
 * Depending on whether a private signing key and/or a public verification key
 * are present a specific instance of the MessageSigner may or may not be able
 * to {@link #sign(Message) sign a message}, {@link #signature(Message)
 * determine a signature for a message} or {@link #verify(Message) verify the
 * signature of a message}.<br>
 * Each MessageSigner {@link #canSignMessages() can sign messages} or
 * {@link #canVerifyMessageSignatures() can verify message signatures}.
 */
public class MessageSigner {

    public static final String DEFAULT_SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String DEFAULT_SIGNATURE_PROVIDER = "SunRsaSign";
    public static final String DEFAULT_SIGNATURE_KEY_ALGORITHM = "RSA";
    public static final int DEFAULT_SIGNATURE_KEY_SIZE = 2048;

    private final String signatureAlgorithm;
    private final String signatureProvider;
    private final String signatureKeyAlgorithm;
    private final int signatureKeySize;

    private final Signature signingSignature;
    private final Signature verificationSignature;

    private final PrivateKey signingKey;
    private final PublicKey verificationKey;

    private MessageSigner(final Builder builder) {
        this.signatureAlgorithm = builder.signatureAlgorithm;
        this.signatureKeyAlgorithm = builder.signatureKeyAlgorithm;
        this.signatureKeySize = builder.signatureKeySize;
        if (builder.signingKey == null && builder.verificationKey == null) {
            throw new IllegalArgumentException(
                    "A signing key (PrivateKey) or verification key (PublicKey) must be provided");
        }
        this.signingKey = builder.signingKey;
        this.verificationKey = builder.verificationKey;
        this.signingSignature = signatureInstance(builder.signatureAlgorithm, builder.signatureProvider,
                builder.signingKey);
        this.verificationSignature = signatureInstance(builder.signatureAlgorithm, builder.signatureProvider,
                builder.verificationKey);
        if (builder.signatureProvider != null) {
            this.signatureProvider = builder.signatureProvider;
        } else if (this.signingSignature != null) {
            this.signatureProvider = this.signingSignature.getProvider().getName();
        } else if (this.verificationSignature != null) {
            this.signatureProvider = this.verificationSignature.getProvider().getName();
        } else {
            // Should not happen, set to null and ignore.
            this.signatureProvider = null;
        }
    }

    public boolean canSignMessages() {
        return this.signingSignature != null;
    }

    /**
     * Signs the provided {@code message}, overwriting an existing signature, if
     * a non-null value is already set.
     *
     * @param message
     * @throws IllegalStateException
     *             if this message signer has a public key for signature
     *             verification, but does not have the private key needed for
     *             signing messages.
     * @throws UncheckedIOException
     *             if determining the bytes for the message throws an
     *             IOException.
     * @throws UncheckedSecurityException
     *             if the signing process throws a SignatureException.
     */
    public void sign(final Message message) {
        final byte[] signatureBytes = this.signature(message);
        message.setSignature(ByteBuffer.wrap(signatureBytes));
    }

    /**
     * Determines the signature for the given {@code message}.
     * <p>
     * The value for the signature in the message will be set to {@code null} to
     * properly determine the signature, but is restored to its original value
     * before this method returns.
     *
     * @param message
     * @return the signature for the message
     * @throws IllegalStateException
     *             if this message signer has a public key for signature
     *             verification, but does not have the private key needed for
     *             signing messages.
     * @throws UncheckedIOException
     *             if determining the bytes for the message throws an
     *             IOException.
     * @throws UncheckedSecurityException
     *             if the signing process throws a SignatureException.
     */
    public byte[] signature(final Message message) {
        if (!this.canSignMessages()) {
            throw new IllegalStateException(
                    "This MessageSigner is not configured for signing, it can only be used for verification");
        }
        final ByteBuffer oldSignature = message.getSignature();
        try {
            message.setSignature(null);
            synchronized (this.signingSignature) {
                this.signingSignature.update(this.toByteBuffer(message));
                return this.signingSignature.sign();
            }
        } catch (final SignatureException e) {
            throw new UncheckedSecurityException("Unable to sign message", e);
        } finally {
            message.setSignature(oldSignature);
        }
    }

    public boolean canVerifyMessageSignatures() {
        return this.verificationSignature != null;
    }

    /**
     * Verifies the signature of the provided {@code message}.
     *
     * @param message
     * @return {@code true} if the signature of the given {@code message} was
     *         verified; {@code false} if not.
     * @throws IllegalStateException
     *             if this message signer has a private key needed for signing
     *             messages, but does not have the public key for signature
     *             verification.
     * @throws UncheckedIOException
     *             if determining the bytes for the message throws an
     *             IOException.
     * @throws UncheckedSecurityException
     *             if the signature verification process throws a
     *             SignatureException.
     */
    public boolean verify(final Message message) {
        if (!this.canVerifyMessageSignatures()) {
            throw new IllegalStateException(
                    "This MessageSigner is not configured for verification, it can only be used for signing");
        }

        final ByteBuffer messageSignature = message.getSignature();
        if (messageSignature == null) {
            return false;
        }
        final byte[] signatureBytes = new byte[messageSignature.remaining()];
        messageSignature.get(signatureBytes);

        try {
            message.setSignature(null);
            synchronized (this.verificationSignature) {
                this.verificationSignature.update(this.toByteBuffer(message));
                return this.verificationSignature.verify(signatureBytes);
            }
        } catch (final SignatureException e) {
            throw new UncheckedSecurityException("Unable to verify message signature", e);
        } finally {
            message.setSignature(messageSignature);
        }
    }

    private ByteBuffer toByteBuffer(final Message message) {
        try {
            return message.toByteBuffer();
        } catch (final IOException e) {
            throw new UncheckedIOException("Unable to determine ByteBuffer for Message", e);
        }
    }

    public String signatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    public String signatureProvider() {
        return this.signatureProvider;
    }

    public String signatureKeyAlgorithm() {
        return this.signatureKeyAlgorithm;
    }

    public int signatureKeySize() {
        return this.signatureKeySize;
    }

    public Optional<PrivateKey> signingKey() {
        return Optional.ofNullable(this.signingKey);
    }

    public Optional<String> signingKeyPem() {
        return this.signingKey().map(key -> this.keyAsMem(key, key.getAlgorithm() + " PRIVATE KEY"));
    }

    public Optional<PublicKey> verificationKey() {
        return Optional.ofNullable(this.verificationKey);
    }

    public Optional<String> verificationKeyPem() {
        return this.verificationKey().map(key -> this.keyAsMem(key, key.getAlgorithm() + " PUBLIC KEY"));
    }

    private String keyAsMem(final Key key, final String label) {
        final StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN ").append(label).append("-----").append("\r\n");
        sb.append(Base64.getMimeEncoder().encodeToString(key.getEncoded())).append("\r\n");
        sb.append("-----END ").append(label).append("-----").append("\r\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("MessageSigner[algorithm=\"%s\"-\"%s\", provider=\"%s\", keySize=%d, sign=%b, verify=%b]",
                this.signatureAlgorithm, this.signatureKeyAlgorithm, this.signatureProvider, this.signatureKeySize,
                this.canSignMessages(), this.canVerifyMessageSignatures());
    }

    public String descriptionWithKeys() {
        final StringBuilder sb = new StringBuilder(this.toString());
        this.signingKeyPem().ifPresent(key -> sb.append(System.lineSeparator()).append(key));
        this.verificationKeyPem().ifPresent(key -> sb.append(System.lineSeparator()).append(key));
        return sb.toString();
    }

    private static final Signature signatureInstance(final String signatureAlgorithm, final String signatureProvider,
            final PrivateKey signingKey) {

        if (signingKey == null) {
            return null;
        }

        final Signature signature = signatureInstance(signatureAlgorithm, signatureProvider);
        try {
            signature.initSign(signingKey);
        } catch (final InvalidKeyException e) {
            throw new UncheckedSecurityException(e);
        }
        return signature;
    }

    private static final Signature signatureInstance(final String signatureAlgorithm, final String signatureProvider,
            final PublicKey verificationKey) {

        if (verificationKey == null) {
            return null;
        }

        final Signature signature = signatureInstance(signatureAlgorithm, signatureProvider);
        try {
            signature.initVerify(verificationKey);
        } catch (final InvalidKeyException e) {
            throw new UncheckedSecurityException(e);
        }
        return signature;
    }

    private static final Signature signatureInstance(final String signatureAlgorithm, final String signatureProvider) {
        try {
            if (signatureProvider == null) {
                return Signature.getInstance(signatureAlgorithm);
            }
            return Signature.getInstance(signatureAlgorithm, signatureProvider);
        } catch (final GeneralSecurityException e) {
            throw new UncheckedSecurityException("Unable to create Signature for Avro Messages", e);
        }
    }

    public static KeyPair generateKeyPair(final String signatureKeyAlgorithm, final String signatureProvider,
            final int signatureKeySize) {
        final KeyPairGenerator keyPairGenerator;
        try {
            if (signatureProvider == null) {
                keyPairGenerator = KeyPairGenerator.getInstance(signatureKeyAlgorithm);
            } else {
                keyPairGenerator = KeyPairGenerator.getInstance(signatureKeyAlgorithm, signatureProvider);
            }
        } catch (final GeneralSecurityException e) {
            throw new UncheckedSecurityException(e);
        }
        keyPairGenerator.initialize(signatureKeySize);
        return keyPairGenerator.generateKeyPair();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private static final Pattern PEM_REMOVAL_PATTERN = Pattern.compile("-----(?:BEGIN|END) .*?-----|\\r|\\n");

        private String signatureAlgorithm = DEFAULT_SIGNATURE_ALGORITHM;
        private String signatureProvider = DEFAULT_SIGNATURE_PROVIDER;
        private String signatureKeyAlgorithm = DEFAULT_SIGNATURE_KEY_ALGORITHM;
        private int signatureKeySize = DEFAULT_SIGNATURE_KEY_SIZE;

        private PrivateKey signingKey = null;
        private PublicKey verificationKey = null;

        public Builder signatureAlgorithm(final String signatureAlgorithm) {
            this.signatureAlgorithm = Objects.requireNonNull(signatureAlgorithm);
            return this;
        }

        public Builder signatureProvider(final String signatureProvider) {
            this.signatureProvider = signatureProvider;
            return this;
        }

        public Builder signatureKeyAlgorithm(final String signatureKeyAlgorithm) {
            this.signatureKeyAlgorithm = Objects.requireNonNull(signatureKeyAlgorithm);
            return this;
        }

        public Builder signatureKeySize(final int signatureKeySize) {
            this.signatureKeySize = signatureKeySize;
            return this;
        }

        public Builder signingKey(final PrivateKey signingKey) {
            this.signingKey = signingKey;
            return this;
        }

        public Builder signingKey(final String signingKeyPem) {
            if (signingKeyPem == null) {
                this.signingKey = null;
                return this;
            }
            final String base64 = PEM_REMOVAL_PATTERN.matcher(signingKeyPem).replaceAll("");
            final byte[] bytes = Base64.getDecoder().decode(base64);
            return this.signingKey(bytes);
        }

        public Builder signingKey(final byte[] signingKeyBytes) {
            if (signingKeyBytes == null) {
                this.signingKey = null;
                return this;
            }
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(signingKeyBytes);
            try {
                this.signingKey = KeyFactory.getInstance(this.signatureKeyAlgorithm).generatePrivate(keySpec);
            } catch (final GeneralSecurityException e) {
                throw new UncheckedSecurityException(e);
            }
            return this;
        }

        public Builder verificationKey(final PublicKey verificationKey) {
            this.verificationKey = verificationKey;
            return this;
        }

        public Builder verificationKey(final String verificationKeyPem) {
            if (verificationKeyPem == null) {
                this.verificationKey = null;
                return this;
            }
            final String base64 = PEM_REMOVAL_PATTERN.matcher(verificationKeyPem).replaceAll("");
            final byte[] bytes = Base64.getDecoder().decode(base64);
            return this.verificationKey(bytes);
        }

        public Builder verificationKey(final byte[] verificationKeyBytes) {
            if (verificationKeyBytes == null) {
                this.verificationKey = null;
                return this;
            }
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(verificationKeyBytes);
            try {
                this.verificationKey = KeyFactory.getInstance(this.signatureKeyAlgorithm).generatePublic(keySpec);
            } catch (final GeneralSecurityException e) {
                throw new UncheckedSecurityException(e);
            }
            return this;
        }

        public Builder keyPair(final KeyPair keyPair) {
            this.signingKey = keyPair.getPrivate();
            this.verificationKey = keyPair.getPublic();
            return this;
        }

        public Builder generateKeyPair() {
            return this.keyPair(MessageSigner.generateKeyPair(this.signatureKeyAlgorithm, this.signatureProvider,
                    this.signatureKeySize));
        }

        public MessageSigner build() {
            return new MessageSigner(this);
        }
    }
}
