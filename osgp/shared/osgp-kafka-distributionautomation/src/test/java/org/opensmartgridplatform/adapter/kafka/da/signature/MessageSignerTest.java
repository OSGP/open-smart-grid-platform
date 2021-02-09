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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.alliander.data.scadameasurementpublishedevent.BaseVoltage;
import com.alliander.data.scadameasurementpublishedevent.ConductingEquipment;
import com.alliander.data.scadameasurementpublishedevent.Message;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;
import com.alliander.messaging.MessageId;

class MessageSignerTest {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String SIGNATURE_PROVIDER = "SunRsaSign";
    private static final String SIGNATURE_KEY_ALGORITHM = "RSA";
    private static final int SIGNATURE_KEY_SIZE = 2048;
    private static final int SIGNATURE_KEY_SIZE_BYTES = SIGNATURE_KEY_SIZE / 8;

    private static final KeyPair KEY_PAIR = MessageSigner.generateKeyPair(SIGNATURE_KEY_ALGORITHM, SIGNATURE_PROVIDER,
            SIGNATURE_KEY_SIZE);

    private static final Random RANDOM = new SecureRandom();

    private final MessageSigner messageSigner = MessageSigner.newBuilder()
            .signatureAlgorithm(SIGNATURE_ALGORITHM)
            .signatureProvider(SIGNATURE_PROVIDER)
            .signatureKeyAlgorithm(SIGNATURE_KEY_ALGORITHM)
            .signatureKeySize(SIGNATURE_KEY_SIZE)
            .keyPair(KEY_PAIR)
            .build();

    @Test
    void signsMessageWithoutSignature() {
        final Message message = this.message();

        this.messageSigner.sign(message);

        assertThat(message.getSignature()).isNotNull();
    }

    @Test
    void signsMessageReplacingSignature() {
        final byte[] randomSignature = this.randomSignature();
        final Message message = this.message(randomSignature, this.payload());

        this.messageSigner.sign(message);

        final byte[] actualSignature = this.bytes(message.getSignature());
        assertThat(actualSignature).isNotNull().isNotEqualTo(randomSignature);
    }

    @Test
    void verifiesMessagesWithValidSignature() {
        final Message message = this.properlySignedMessage();

        final boolean signatureWasVerified = this.messageSigner.verify(message);

        assertThat(signatureWasVerified).isTrue();
    }

    @Test
    void doesNotVerifyMessagesWithoutSignature() {
        final Message message = this.message();

        final boolean signatureWasVerified = this.messageSigner.verify(message);

        assertThat(signatureWasVerified).isFalse();
    }

    @Test
    void doesNotVerifyMessagesWithIncorrectSignature() {
        final byte[] randomSignature = this.randomSignature();
        final Message message = this.message(randomSignature, this.payload());

        final boolean signatureWasVerified = this.messageSigner.verify(message);

        assertThat(signatureWasVerified).isFalse();
    }

    private String fromPemResource(final String name) {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream(name), StandardCharsets.ISO_8859_1)).lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Test
    void worksWithKeysFromPemEncodedResources() {

        final MessageSigner messageSignerWithKeysFromResources = MessageSigner.newBuilder()
                .signatureAlgorithm(SIGNATURE_ALGORITHM)
                .signatureProvider(SIGNATURE_PROVIDER)
                .signatureKeyAlgorithm(SIGNATURE_KEY_ALGORITHM)
                .signatureKeySize(SIGNATURE_KEY_SIZE)
                .signingKey(this.fromPemResource("/signing-key.pem"))
                .verificationKey(this.fromPemResource("/verification-key.pem"))
                .build();

        final Message message = this.message();
        messageSignerWithKeysFromResources.sign(message);
        final boolean signatureWasVerified = messageSignerWithKeysFromResources.verify(message);

        assertThat(signatureWasVerified).isTrue();
    }

    private Message message() {
        return this.message(null, this.payload());
    }

    private Message properlySignedMessage() {
        final Message message = this.message(null, this.payload());
        this.messageSigner.sign(message);
        return message;
    }

    private Message message(final byte[] signature, final ScadaMeasurementPublishedEvent payload) {
        return Message.newBuilder()
                .setMessageId(this.messageId())
                .setCreatedDateTime(System.currentTimeMillis())
                .setProducerId("GXF-test")
                .setSignature(this.byteBuffer(signature))
                .setPayload(payload)
                .build();
    }

    private MessageId messageId() {
        final UUID uuid = UUID.randomUUID();
        final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return new MessageId(bb.array());
    }

    private byte[] randomSignature() {
        final byte[] signature = new byte[SIGNATURE_KEY_SIZE_BYTES];
        RANDOM.nextBytes(signature);
        return signature;
    }

    private ScadaMeasurementPublishedEvent payload() {

        return ScadaMeasurementPublishedEvent.newBuilder()
                .setMeasurements(new ArrayList<>())
                .setPowerSystemResourceBuilder(ConductingEquipment.newBuilder()
                        .setBaseVoltageBuilder(BaseVoltage.newBuilder()
                                .setDescription("BaseVoltage description")
                                .setNominalVoltage(null))
                        .setNames(new ArrayList<>()))
                .setCreatedDateTime(System.currentTimeMillis())
                .setDescription("ScadaMeasurementPublishedEvent description")
                .setMRID(UUID.randomUUID().toString())
                .build();
    }

    private byte[] bytes(final ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return null;
        }
        final byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return bytes;
    }

    private ByteBuffer byteBuffer(final byte[] bytes) {
        return bytes == null ? null : ByteBuffer.wrap(bytes);
    }
}
