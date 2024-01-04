// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.xml.bind.DatatypeConverter;
import lombok.Builder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

class Mx382AlarmDecoderTest {

  private final byte[] obiscode =
      new byte[] {(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x00};
  private final String equipmentIdentifier = "KA6P005039021110";
  private final byte preconfiguredClient = 0x66;
  private final byte apduType = (byte) 0xC2;

  @ParameterizedTest
  @ValueSource(strings = {"KA6P005039021110", "KAL70005088116712"})
  void decodeWithoutDate(final String deviceIdentification)
      throws UnrecognizedMessageDataException {

    final byte[] mx382message =
        Mx382AlarmMessage.builder()
            .equipmentIdentifier(deviceIdentification)
            .obiscode(this.obiscode)
            .client(this.preconfiguredClient)
            .apduType(this.apduType)
            .build()
            .encode();
    final Mx382AlarmDecoder decoder = new Mx382AlarmDecoder();
    final InputStream is = new ByteArrayInputStream(mx382message);
    final DlmsPushNotification dlmsPushNotification =
        decoder.decodeMx382alarm(is, ConnectionProtocol.TCP);
    assertThat(dlmsPushNotification.getAlarms()).hasSize(1);
    assertThat(dlmsPushNotification.getAlarms().iterator().next())
        .isEqualTo(AlarmTypeDto.LAST_GASP);
    assertThat(dlmsPushNotification.getTriggerType()).isEqualTo(AlarmDecoder.PUSH_ALARM_TRIGGER);
    assertThat(dlmsPushNotification.getEquipmentIdentifier()).isEqualTo(deviceIdentification);
  }

  /*
   * Decode with an alarm message that contains a date time stamp, this has an alternative length within the WDPU header (2a on index 7, 8th byte).
   * Build an alarm message with an exact message from the log with spaces removed.
   * +----------------------------------------------------------|----------------+
   * |        | 0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |                |
   * +--------+-------------------------------------------------+----------------+
   * |00000000| 00 01 00 67 00 66 00 2a c2 09 0c 07 e7 07 06 04 |...g.f.*........|
   * |00000010| 0a 36 26 00 ff 88 80 00 01 80 80 80 80 80 00 02 |.6&.............|
   * |00000020| 09 10 4b 41 36 50 30 30 35 30 33 39 30 32 31 31 |..KA6P0050390211|
   * |00000030| 31 30                                           |10              |
   * +--------+-------------------------------------------------+----------------+
   */

  @Test
  void decodeWithDateTimeShouldPass() throws UnrecognizedMessageDataException {
    final String message =
        "000100670066002ac2090c07e70706040a362600ff888000018080808080000209104b413650303035303339303231313130";

    final byte[] mx382message =
        Mx382AlarmMessage.builder() //
            .message(message)
            .build()
            .encode();

    final Mx382AlarmDecoder decoder = new Mx382AlarmDecoder();
    final InputStream is = new ByteArrayInputStream(mx382message);
    final DlmsPushNotification dlmsPushNotification =
        decoder.decodeMx382alarm(is, ConnectionProtocol.TCP);
    assertThat(dlmsPushNotification.getAlarms()).hasSize(1);
    assertThat(dlmsPushNotification.getAlarms().iterator().next())
        .isEqualTo(AlarmTypeDto.LAST_GASP);
    assertThat(dlmsPushNotification.getTriggerType()).isEqualTo(AlarmDecoder.PUSH_ALARM_TRIGGER);
    assertThat(dlmsPushNotification.getEquipmentIdentifier()).isEqualTo(this.equipmentIdentifier);
  }

  @Test
  void decodeWakeupAlarm() throws UnrecognizedMessageDataException {
    final String deviceIdentification = "KAL7005088116712";
    final String message =
        "000100670066002ac2090c07e70a19030b122900ff888000010000600101ff0209104b414c37303035303838313136373132";

    final byte[] mx382message =
        Mx382AlarmMessage.builder() //
            .message(message)
            .build()
            .encode();

    final Mx382AlarmDecoder decoder = new Mx382AlarmDecoder();
    final InputStream is = new ByteArrayInputStream(mx382message);
    final DlmsPushNotification dlmsPushNotification =
        decoder.decodeMx382alarm(is, ConnectionProtocol.UDP);
    assertThat(dlmsPushNotification.getAlarms()).isEmpty();
    assertThat(dlmsPushNotification.getTriggerType()).isEqualTo(AlarmDecoder.PUSH_SMS_TRIGGER);
    assertThat(dlmsPushNotification.getEquipmentIdentifier()).isEqualTo(deviceIdentification);
  }

  @Test
  void invalidDeviceIdentificationLength() throws UnrecognizedMessageDataException {
    final String deviceIdentification = "KAL123";

    final byte[] mx382message =
        Mx382AlarmMessage.builder()
            .equipmentIdentifier(deviceIdentification)
            .obiscode(this.obiscode)
            .client(this.preconfiguredClient)
            .apduType(this.apduType)
            .build()
            .encode();
    final Mx382AlarmDecoder decoder = new Mx382AlarmDecoder();
    final InputStream is = new ByteArrayInputStream(mx382message);
    final UnrecognizedMessageDataException exception =
        assertThrows(
            UnrecognizedMessageDataException.class,
            () -> {
              decoder.decodeMx382alarm(is, ConnectionProtocol.TCP);
            });
    assertThat(exception.getMessage())
        .contains(
            "Data in DLMS Push Notification cannot be decoded. Reason: Expected length value of 29,30,42 or 43, but found length of 19");
  }

  @Test
  void testCheckOnObiscode() {

    final byte[] incorrectObiscode =
        new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    final byte[] mx382message =
        Mx382AlarmMessage.builder()
            .equipmentIdentifier(this.equipmentIdentifier)
            .obiscode(incorrectObiscode)
            .client(this.preconfiguredClient)
            .apduType(this.apduType)
            .build()
            .encode();
    assertDecodingException(mx382message);
  }

  @Test
  void testCheckOnHeader() {

    final byte[] mx382message =
        Mx382AlarmMessage.builder()
            .equipmentIdentifier(this.equipmentIdentifier)
            .obiscode(this.obiscode)
            .client((byte) 0x00)
            .apduType(this.apduType)
            .build()
            .encode();
    assertDecodingException(mx382message);
  }

  @Test
  void testCheckOnApduType() {

    final byte[] mx382message =
        Mx382AlarmMessage.builder()
            .equipmentIdentifier(this.equipmentIdentifier)
            .obiscode(this.obiscode)
            .client(this.preconfiguredClient)
            .apduType((byte) 0x10)
            .build()
            .encode();
    assertDecodingException(mx382message);
  }

  private static void assertDecodingException(final byte[] mx382message) {
    final Mx382AlarmDecoder decoder = new Mx382AlarmDecoder();
    final InputStream is = new ByteArrayInputStream(mx382message);

    final Throwable actual =
        catchThrowable(() -> decoder.decodeMx382alarm(is, ConnectionProtocol.TCP));
    assertThat(actual).isInstanceOf(UnrecognizedMessageDataException.class);
  }

  @Builder
  public static final class Mx382AlarmMessage {

    public static final int DATA_TYPE_NULL_DATA = 0x00;

    private final String equipmentIdentifier;

    private final byte[] obiscode;
    private final byte client;
    private final byte apduType;

    private final String message;

    protected Mx382AlarmMessage(
        final String equipmentIdentifier,
        final byte[] obiscode,
        final byte client,
        final byte apduType,
        final String message) {
      this.equipmentIdentifier = equipmentIdentifier;
      this.obiscode = obiscode;
      this.client = client;
      this.apduType = apduType;
      this.message = message;
    }

    public byte[] encode() {
      if (this.message != null) {
        return DatatypeConverter.parseHexBinary(this.message);
      }
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
      buf.put((byte) DATA_TYPE_NULL_DATA);

      final byte[] classId = Mx382AlarmDecoder.CLASS_ID_1;
      buf.put(classId);
      buf.put(this.obiscode);
      final byte attribute = Mx382AlarmDecoder.ATTRIBUTE_ID_2;
      buf.put((byte) attribute);
      buf.put((byte) Mx382AlarmDecoder.DATA_TYPE_OCTET_STRING);
      buf.put((byte) this.equipmentIdentifier.getBytes().length);
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
