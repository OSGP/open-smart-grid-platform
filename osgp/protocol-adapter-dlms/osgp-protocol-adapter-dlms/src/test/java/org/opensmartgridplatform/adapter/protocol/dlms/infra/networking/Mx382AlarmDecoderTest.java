// Copyright 2023 Alliander N.V.
// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

class Mx382AlarmDecoderTest {

  private final byte[] obiscode =
      new byte[] {(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x00};
  private final String equipmentIdentifier = "KA6P005039021110";
  private final byte preconfiguredClient = 0x66;
  private final byte apduType = (byte) 0xC2;

  @Test
  void testDecode() throws UnrecognizedMessageDataException {

    final byte[] mx382message =
        new Mx382AlarmMessage(
                this.equipmentIdentifier, this.obiscode, this.preconfiguredClient, this.apduType)
            .encode();
    final Mx382AlarmDecoder decoder = new Mx382AlarmDecoder();
    final InputStream is = new ByteArrayInputStream(mx382message);
    final DlmsPushNotification dlmsPushNotification = decoder.decodeMx382alarm(is);
    assertThat(dlmsPushNotification.getAlarms()).hasSize(1);
    assertThat(dlmsPushNotification.getAlarms().iterator().next())
        .isEqualTo(AlarmTypeDto.LAST_GASP);
    assertThat(dlmsPushNotification.getTriggerType()).isEqualTo(AlarmDecoder.PUSH_ALARM_TRIGGER);
    assertThat(dlmsPushNotification.getEquipmentIdentifier()).isEqualTo(this.equipmentIdentifier);
  }

  @Test
  void testCheckOnObiscode() {
    final byte[] incorrectObiscode =
        new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    final byte[] mx382message =
        new Mx382AlarmMessage(
                this.equipmentIdentifier,
                incorrectObiscode,
                this.preconfiguredClient,
                this.apduType)
            .encode();
    assertDecodingException(mx382message);
  }

  @Test
  void testCheckOnHeader() {

    final byte[] mx382message =
        new Mx382AlarmMessage(this.equipmentIdentifier, this.obiscode, (byte) 0x00, this.apduType)
            .encode();
    assertDecodingException(mx382message);
  }

  @Test
  void testCheckOnApduType() {

    final byte[] mx382message =
        new Mx382AlarmMessage(
                this.equipmentIdentifier, this.obiscode, this.preconfiguredClient, (byte) 0x10)
            .encode();
    assertDecodingException(mx382message);
  }

  private static void assertDecodingException(final byte[] mx382message) {
    final Mx382AlarmDecoder decoder = new Mx382AlarmDecoder();
    final InputStream is = new ByteArrayInputStream(mx382message);

    final Throwable actual = catchThrowable(() -> decoder.decodeMx382alarm(is));
    assertThat(actual).isInstanceOf(UnrecognizedMessageDataException.class);
  }

  public static final class Mx382AlarmMessage {
    private final String equipmentIdentifier;

    private final byte[] obiscode;
    private final byte client;
    private final byte apduType;

    public Mx382AlarmMessage(
        final String equipmentIdentifier,
        final byte[] obiscode,
        final byte client,
        final byte apduType) {
      this.equipmentIdentifier = equipmentIdentifier;
      this.obiscode = obiscode;
      this.client = client;
      this.apduType = apduType;
    }

    public byte[] encode() {
      final byte[] apdu = this.createApdu();
      final byte[] header = this.createWpduHeader(apdu.length);
      final byte[] wnm = new byte[header.length + apdu.length];
      System.arraycopy(header, 0, wnm, 0, header.length);
      System.arraycopy(apdu, 0, wnm, header.length, apdu.length);
      return wnm;
    }

    private byte[] createApdu() {
      final ByteBuffer buf = ByteBuffer.allocate(64);
      buf.put(this.apduType);
      buf.put((byte) 0x00);

      final byte[] classId = new byte[] {0x00, 0x01};
      buf.put(classId);
      buf.put(this.obiscode);
      final byte attribute = 0x02;
      buf.put((byte) attribute);
      buf.put(this.equipmentIdentifier.getBytes());
      buf.flip();
      final byte[] bytes = new byte[buf.limit()];
      buf.get(bytes);
      return bytes;
    }

    private byte[] createWpduHeader(final int apduLength) {
      return new byte[] {0x00, 0x01, 0x00, 0x67, 0x00, this.client, 0x00, (byte) apduLength};
    }
  }
}
