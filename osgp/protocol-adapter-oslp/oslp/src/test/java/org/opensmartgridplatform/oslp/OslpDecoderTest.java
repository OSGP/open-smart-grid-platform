/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.oslp;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ResourceLeakDetector;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.oslp.Oslp.ConfirmRegisterDeviceRequest;
import org.opensmartgridplatform.oslp.Oslp.DeviceType;
import org.opensmartgridplatform.oslp.Oslp.Event;
import org.opensmartgridplatform.oslp.Oslp.EventNotification;
import org.opensmartgridplatform.oslp.Oslp.EventNotificationRequest;
import org.opensmartgridplatform.oslp.Oslp.Message;
import org.opensmartgridplatform.oslp.Oslp.RegisterDeviceRequest;

class OslpDecoderTest {
  private static final int MAX_RANDOM_DEVICE_OR_PLATFORM = 65535;
  private static final String ALGORITHM_DETAILS = "SHA256withECDSA";
  private static final String PROVIDER_DETAILS = "SunEC";

  private final Random random = new SecureRandom();

  private KeyPair keyPair;
  private byte[] sequenceNumber;
  private byte[] deviceId;
  private String deviceIdentification;
  private Message message;

  @BeforeEach
  void setUp() {
    ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    this.keyPair = this.getKeyPair();
    this.sequenceNumber = this.randomSequenceNumber();
    this.deviceId = this.randomDeviceId();
    this.deviceIdentification = "device-identification";
    this.message = this.randomMessage();
  }

  @Test
  void decodesAnOslpEnvelopeWhenAllBytesAreReceivedTogether() {
    final EmbeddedChannel channel = new EmbeddedChannel(this.oslpDecoder());
    final OslpEnvelope oslpEnvelope = this.oslpEnvelope();

    ConnectionBehavior.ALL_BYTES_TOGETHER.writeOslpEnvelope(channel, oslpEnvelope);
    final OslpEnvelope actualOslpEnvelope = channel.readInbound();

    final String description = this.oslpEnvelopeDetails(oslpEnvelope, this.keyPair.getPrivate());
    this.assertActualOslpEnvelopeEqualsExpected(actualOslpEnvelope, oslpEnvelope, description);
  }

  @Test
  void decodesAnOslpEnvelopeWhenAllBytesAreReceivedInChunksMatchingTheParts() {
    final EmbeddedChannel channel = new EmbeddedChannel(this.oslpDecoder());
    final OslpEnvelope oslpEnvelope = this.oslpEnvelope();

    ConnectionBehavior.FIELD_MATCHING_CHUNKS.writeOslpEnvelope(channel, oslpEnvelope);
    final OslpEnvelope actualOslpEnvelope = channel.readInbound();

    final String description = this.oslpEnvelopeDetails(oslpEnvelope, this.keyPair.getPrivate());
    this.assertActualOslpEnvelopeEqualsExpected(actualOslpEnvelope, oslpEnvelope, description);
  }

  @Test
  void decodesAnOslpEnvelopeWhenAllBytesAreReceivedOneByOne() {
    final EmbeddedChannel channel = new EmbeddedChannel(this.oslpDecoder());
    final OslpEnvelope oslpEnvelope = this.oslpEnvelope();

    ConnectionBehavior.SINGLE_BYTE_CHUNKS.writeOslpEnvelope(channel, oslpEnvelope);
    final OslpEnvelope actualOslpEnvelope = channel.readInbound();

    final String description = this.oslpEnvelopeDetails(oslpEnvelope, this.keyPair.getPrivate());
    this.assertActualOslpEnvelopeEqualsExpected(actualOslpEnvelope, oslpEnvelope, description);
  }

  @Test
  void decodesAnOslpEnvelopeWhenAllBytesAreReceivedInRandomChunks() {
    final EmbeddedChannel channel = new EmbeddedChannel(this.oslpDecoder());
    final OslpEnvelope oslpEnvelope = this.oslpEnvelope();

    ConnectionBehavior.RANDOM_CHUNKS.writeOslpEnvelope(channel, oslpEnvelope);
    final OslpEnvelope actualOslpEnvelope = channel.readInbound();

    final String description = this.oslpEnvelopeDetails(oslpEnvelope, this.keyPair.getPrivate());
    this.assertActualOslpEnvelopeEqualsExpected(actualOslpEnvelope, oslpEnvelope, description);
  }

  @Test
  void decodesAnOslpEnvelopeEncodedByOslpEncoder() {
    final EmbeddedChannel channel =
        new EmbeddedChannel(new InboundOslpEncoder(), this.oslpDecoder());
    final OslpEnvelope oslpEnvelope = this.oslpEnvelope();

    assertThat(channel.writeInbound(oslpEnvelope)).isTrue();
    final OslpEnvelope actualOslpEnvelope = channel.readInbound();

    final String description = this.oslpEnvelopeDetails(oslpEnvelope, this.keyPair.getPrivate());
    this.assertActualOslpEnvelopeEqualsExpected(actualOslpEnvelope, oslpEnvelope, description);
  }

  @Test
  void decodesMultipleOslpEnvelopesInSequence() {
    final EmbeddedChannel channel = new EmbeddedChannel(this.oslpDecoder());
    final List<OslpEnvelope> oslpEnvelopes = this.oslpEnvelopes();
    final List<String> descriptions = new ArrayList<>();

    for (final OslpEnvelope oslpEnvelope : oslpEnvelopes) {
      descriptions.add(this.oslpEnvelopeDetails(oslpEnvelope, this.keyPair.getPrivate()));
      this.randomConnectionBehavior().writeOslpEnvelope(channel, oslpEnvelope);
    }

    final int numberOfEnvelopes = oslpEnvelopes.size();
    final StringBuilder descriptionBuilder = new StringBuilder();
    int index = 1;
    for (final String description : descriptions) {
      descriptionBuilder.append(
          String.format("OslpEnvelope [%d of %d]:%n%s%n", index, numberOfEnvelopes, description));
      index += 1;
    }
    for (int i = 0; i < numberOfEnvelopes; i++) {
      final OslpEnvelope expectedOslpEnvelope = oslpEnvelopes.get(i);
      final OslpEnvelope actualOslpEnvelope = channel.readInbound();
      final String description =
          String.format(
              "%s%nComparing OslpEnvelope %d of %d", descriptionBuilder, i + 1, numberOfEnvelopes);
      this.assertActualOslpEnvelopeEqualsExpected(
          actualOslpEnvelope, expectedOslpEnvelope, description);
    }
  }

  private OslpEnvelope oslpEnvelope() {
    return new OslpEnvelope.Builder()
        .withSignature(ALGORITHM_DETAILS)
        .withProvider(PROVIDER_DETAILS)
        .withPrimaryKey(this.keyPair.getPrivate())
        .withSecurityKey(null /* will be generated */)
        .withSequenceNumber(this.sequenceNumber)
        .withDeviceId(this.deviceId)
        .withPayloadMessage(this.message)
        .build();
  }

  private List<OslpEnvelope> oslpEnvelopes() {
    final List<OslpEnvelope> envelopes = new ArrayList<>();
    final OslpEnvelope.Builder builder =
        new OslpEnvelope.Builder()
            .withSignature(ALGORITHM_DETAILS)
            .withProvider(PROVIDER_DETAILS)
            .withPrimaryKey(this.keyPair.getPrivate())
            .withSecurityKey(null /* will be generated */)
            .withDeviceId(this.deviceId);

    final ByteBuffer bb = ByteBuffer.allocate(2);
    short sequenceNumberValue = (short) this.random.nextInt();
    this.sequenceNumber = bb.putShort(sequenceNumberValue).array();
    this.message = OslpMessageType.REGISTER_DEVICE_REQUEST.randomMessage(this);
    envelopes.add(
        builder.withSequenceNumber(this.sequenceNumber).withPayloadMessage(this.message).build());

    bb.rewind();
    sequenceNumberValue += 1;
    this.sequenceNumber = bb.putShort(sequenceNumberValue).array();
    this.message = OslpMessageType.CONFIRM_REGISTER_DEVICE_REQUEST.randomMessage(this);
    envelopes.add(
        builder.withSequenceNumber(this.sequenceNumber).withPayloadMessage(this.message).build());

    bb.rewind();
    sequenceNumberValue += 1;
    this.sequenceNumber = bb.putShort(sequenceNumberValue).array();
    this.message = OslpMessageType.EVENT_NOTIFICATION_REQUEST.randomMessage(this);
    envelopes.add(
        builder.withSequenceNumber(this.sequenceNumber).withPayloadMessage(this.message).build());

    return envelopes;
  }

  private OslpDecoder oslpDecoder() {
    return new OslpDecoder(ALGORITHM_DETAILS, PROVIDER_DETAILS);
  }

  private OslpMessageType randomOslpMessageType() {
    final OslpMessageType[] values = OslpMessageType.values();
    return values[this.random.nextInt(values.length)];
  }

  private ConnectionBehavior randomConnectionBehavior() {
    final ConnectionBehavior[] values = ConnectionBehavior.values();
    return values[this.random.nextInt(values.length)];
  }

  private KeyPair getKeyPair() {
    final ECGenParameterSpec parameterSpec = new ECGenParameterSpec("secp256r1");
    try {
      final KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
      generator.initialize(parameterSpec, new SecureRandom());
      return generator.generateKeyPair();
    } catch (final GeneralSecurityException e) {
      throw new AssertionError("Unable to generate key pair", e);
    }
  }

  private byte[] randomSequenceNumber() {
    final byte[] randomSequenceNumber = new byte[OslpEnvelope.SEQUENCE_NUMBER_LENGTH];
    this.random.nextBytes(randomSequenceNumber);
    return randomSequenceNumber;
  }

  private int randomDeviceOrPlatform() {
    return this.random.nextInt(MAX_RANDOM_DEVICE_OR_PLATFORM + 1);
  }

  private byte[] randomDeviceId() {
    final byte[] randomDeviceUid =
        new byte[OslpEnvelope.MANUFACTURER_ID_LENGTH + OslpEnvelope.DEVICE_ID_LENGTH];
    this.random.nextBytes(randomDeviceUid);
    return randomDeviceUid;
  }

  private Message randomMessage() {
    return this.randomOslpMessageType().randomMessage(this);
  }

  private Event randomEvent() {
    final Event[] values = Event.values();
    return values[this.random.nextInt(values.length)];
  }

  private ByteString randomEventIndex() {
    return ByteString.copyFrom(new byte[] {(byte) this.random.nextInt(4)});
  }

  private String oslpEnvelopeDetails(final OslpEnvelope oslpEnvelope, final PrivateKey privateKey) {
    if (oslpEnvelope == null) {
      return "no OslpEnvelope";
    }
    final String privateKeyDetails = this.privateKeyDetails(privateKey);
    final String securityKeyDetails = this.byteArrayDetails(oslpEnvelope.getSecurityKey());
    final String sequenceNumberDetails = this.byteArrayDetails(oslpEnvelope.getSequenceNumber());
    final String deviceIdDetails = this.deviceIdDetails(oslpEnvelope.getDeviceId());
    final String lengthIndicatorDetails = this.byteArrayDetails(oslpEnvelope.getLengthIndicator());

    return String.format(
        "OslpEnvelope[%n\tsignatureAlgorithm: %s%n\tprovider: %s%n\tprivateKey: %s%n\tsize: %d"
            + "%n\tsecurityKey: %s%n\tsequenceNumber: %s%n\tdeviceId: %s%n\tlengthIndicator: %s"
            + "%n\tpayloadMessage: %s(end of OslpEnvelope)]",
        ALGORITHM_DETAILS,
        PROVIDER_DETAILS,
        privateKeyDetails,
        oslpEnvelope.getSize(),
        securityKeyDetails,
        sequenceNumberDetails,
        deviceIdDetails,
        lengthIndicatorDetails,
        oslpEnvelope.getPayloadMessage());
  }

  private String deviceIdDetails(final byte[] deviceId) {
    if (deviceId == null || deviceId.length == 0) {
      return this.byteArrayDetails(deviceId);
    }
    return String.format(
        "%s (Hex), %s (Base64)",
        Hex.encodeHexString(deviceId), Base64.getEncoder().encodeToString(deviceId));
  }

  private String byteArrayDetails(final byte[] bytes) {
    if (bytes == null) {
      return "null";
    }
    if (bytes.length == 0) {
      return "empty";
    }
    return String.format("%s (Hex)", Hex.encodeHexString(bytes));
  }

  private String privateKeyDetails(final PrivateKey privateKey) {
    if (privateKey == null) {
      return "no private key";
    }
    return String.format(
        "algorithm = %s, format = %s, encoded = %s",
        privateKey.getAlgorithm(),
        privateKey.getFormat(),
        this.byteArrayDetails(privateKey.getEncoded()));
  }

  private void assertActualOslpEnvelopeEqualsExpected(
      final OslpEnvelope actual, final OslpEnvelope expected, final String description) {

    assertThat(actual)
        .usingRecursiveComparison()
        /*
         * Fields privateKey and valid are related to creation or
         * validation of the cryptographic signature (securityKey) of
         * the OslpEnvelope, and are not part of the message that is
         * sent as bytes over the channel, so any existing values other
         * than the defaults won't show up from OslpEnvelopes produced
         * by the OslpDecoder.
         */
        .ignoringFields("privateKey", "valid")
        .as(description)
        .isEqualTo(expected);
  }

  enum ConnectionBehavior {
    ALL_BYTES_TOGETHER {
      @Override
      public void writeOslpEnvelope(final EmbeddedChannel channel, final OslpEnvelope envelope) {
        assertThat(channel.writeInbound(byteBuf(envelope))).isTrue();
      }
    },
    FIELD_MATCHING_CHUNKS {
      @Override
      public void writeOslpEnvelope(final EmbeddedChannel channel, final OslpEnvelope envelope) {
        channel.writeInbound(Unpooled.copiedBuffer(envelope.getSecurityKey()));
        channel.writeInbound(Unpooled.copiedBuffer(envelope.getSequenceNumber()));
        channel.writeInbound(Unpooled.copiedBuffer(envelope.getDeviceId()));
        channel.writeInbound(Unpooled.copiedBuffer(envelope.getLengthIndicator()));
        assertThat(
                channel.writeInbound(
                    Unpooled.copiedBuffer(envelope.getPayloadMessage().toByteArray())))
            .isTrue();
      }
    },
    SINGLE_BYTE_CHUNKS {
      @Override
      public void writeOslpEnvelope(final EmbeddedChannel channel, final OslpEnvelope envelope) {
        final ByteBuf byteBuf = byteBuf(envelope);
        final int numberOfBytes = byteBuf.array().length;
        for (int index = 0; index < numberOfBytes - 1; index++) {
          channel.writeInbound(byteBuf.copy(index, 1));
        }
        assertThat(channel.writeInbound(byteBuf.copy(numberOfBytes - 1, 1))).isTrue();
      }
    },
    RANDOM_CHUNKS {

      private final Random random = new SecureRandom();

      private int[] splitInRandomLengths(final int totalLength) {
        final int[] lengths = new int[totalLength];
        int numberOfLengths = 0;
        int sumOfLengths = 0;
        while (sumOfLengths < totalLength) {
          final int nextLength;
          if (sumOfLengths == totalLength - 1) {
            nextLength = 1;
          } else {
            nextLength = 1 + this.random.nextInt(totalLength - sumOfLengths - 1);
          }
          lengths[numberOfLengths] = nextLength;
          numberOfLengths += 1;
          sumOfLengths += nextLength;
        }
        return Arrays.copyOf(lengths, numberOfLengths);
      }

      @Override
      public void writeOslpEnvelope(final EmbeddedChannel channel, final OslpEnvelope envelope) {
        final ByteBuf byteBuf = byteBuf(envelope);
        final int numberOfBytes = byteBuf.array().length;
        final int[] chunkSizes = this.splitInRandomLengths(numberOfBytes);
        final int numberOfChunks = chunkSizes.length;
        int index = 0;
        for (int i = 0; i < numberOfChunks - 1; i++) {
          final int length = chunkSizes[i];
          channel.writeInbound(byteBuf.copy(index, length));
          index += length;
        }
        assertThat(channel.writeInbound(byteBuf.copy(index, chunkSizes[numberOfChunks - 1])))
            .isTrue();
      }
    };

    public static ByteBuf byteBuf(final OslpEnvelope envelope) {
      return Unpooled.copiedBuffer(
          envelope.getSecurityKey(),
          envelope.getSequenceNumber(),
          envelope.getDeviceId(),
          envelope.getLengthIndicator(),
          envelope.getPayloadMessage().toByteArray());
    }

    public abstract void writeOslpEnvelope(
        final EmbeddedChannel channel, final OslpEnvelope envelope);
  }

  enum OslpMessageType {
    REGISTER_DEVICE_REQUEST {
      @Override
      public Message randomMessage(final OslpDecoderTest test) {
        return Message.newBuilder()
            .setRegisterDeviceRequest(
                RegisterDeviceRequest.newBuilder()
                    .setDeviceIdentification(test.deviceIdentification)
                    .setIpAddress(ByteString.copyFrom(new byte[] {127, 0, 0, 1}))
                    .setDeviceType(DeviceType.SSLD)
                    .setHasSchedule(false)
                    .setRandomDevice(test.randomDeviceOrPlatform()))
            .build();
      }
    },
    CONFIRM_REGISTER_DEVICE_REQUEST {
      @Override
      public Message randomMessage(final OslpDecoderTest test) {
        return Message.newBuilder()
            .setConfirmRegisterDeviceRequest(
                ConfirmRegisterDeviceRequest.newBuilder()
                    .setRandomDevice(test.randomDeviceOrPlatform())
                    .setRandomPlatform(test.randomDeviceOrPlatform()))
            .build();
      }
    },
    EVENT_NOTIFICATION_REQUEST {
      private final DateTimeFormatter oslpTimestampFormatter =
          DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

      @Override
      public Message randomMessage(final OslpDecoderTest test) {
        final Event event = test.randomEvent();
        final EventNotificationRequest.Builder eventNotificationRequestBuilder =
            EventNotificationRequest.newBuilder();
        final int numberOfEvents = 1 + test.random.nextInt(6);
        for (int i = 0; i < numberOfEvents; i++) {
          final EventNotification.Builder eventNotificationBuilder =
              EventNotification.newBuilder()
                  .setEvent(event)
                  .setDescription(
                      test.random.nextBoolean()
                          ? ""
                          : String.format("%s [%d/%d] used in test", event, i + 1, numberOfEvents))
                  .setIndex(test.randomEventIndex());
          if (test.random.nextBoolean()) {
            eventNotificationBuilder.setTimestamp(
                this.oslpTimestampFormatter.format(LocalDateTime.now()));
          }
          eventNotificationRequestBuilder.addNotifications(eventNotificationBuilder);
        }
        return Message.newBuilder()
            .setEventNotificationRequest(eventNotificationRequestBuilder)
            .build();
      }
    };

    public abstract Message randomMessage(final OslpDecoderTest test);
  }
}
