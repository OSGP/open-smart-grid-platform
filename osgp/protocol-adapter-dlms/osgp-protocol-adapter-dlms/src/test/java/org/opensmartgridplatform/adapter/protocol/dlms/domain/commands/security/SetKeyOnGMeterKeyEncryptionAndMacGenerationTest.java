//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

@ExtendWith(MockitoExtension.class)
class SetKeyOnGMeterKeyEncryptionAndMacGenerationTest {

  private final String DEVICE_IDENTIFICATION_G = "G-meter DeviceId";
  private final DlmsDevice DEVICE_G =
      this.createDlmsDevice(Protocol.SMR_5_0_0, this.DEVICE_IDENTIFICATION_G);
  private final int KEY_SIZE = 16;
  private final int KEY_DATA_SIZE_SMR5 = 30; // KEY_SIZE + 14 bytes (kcc, keyId, keySize and mac)
  private static final int KCC_LENGTH = 4;

  final byte[] MASTER_KEY =
      new byte[] {
        (byte) 0x00,
        (byte) 0x01,
        (byte) 0x02,
        (byte) 0x03,
        (byte) 0x04,
        (byte) 0x05,
        (byte) 0x06,
        (byte) 0x07,
        (byte) 0x08,
        (byte) 0x09,
        (byte) 0x0A,
        (byte) 0x0B,
        (byte) 0x0C,
        (byte) 0x0D,
        (byte) 0x0E,
        (byte) 0x0F
      };
  final byte[] NEW_KEY =
      new byte[] {
        (byte) 0x11,
        (byte) 0x11,
        (byte) 0x11,
        (byte) 0x11,
        (byte) 0x22,
        (byte) 0x22,
        (byte) 0x22,
        (byte) 0x22,
        (byte) 0x33,
        (byte) 0x33,
        (byte) 0x33,
        (byte) 0x33,
        (byte) 0x44,
        (byte) 0x44,
        (byte) 0x44,
        (byte) 0x55
      };

  private final SetKeyOnGMeterKeyEncryptionAndMacGeneration macGeneration =
      new SetKeyOnGMeterKeyEncryptionAndMacGeneration();

  @Test
  void testEncryptDsmr4() throws ProtocolAdapterException {
    final byte[] encryptedKey =
        this.macGeneration.encryptMbusUserKeyDsmr4(this.MASTER_KEY, this.NEW_KEY);

    assertThat(encryptedKey)
        .containsExactly(
            0x08, 0xea, 0xb1, 0x84, 0x36, 0x31, 0x41, 0xf1, 0xe7, 0xb5, 0x54, 0x93, 0x9d, 0xfb,
            0x75, 0xb8);
  }

  /* Verified example for SMR5 P0 key:
   * MASTER_KEY = 0x000102030405060708090a0b0c0d0e0f
   * NEW_USER_KEY = 0x11111111222222223333333344444455
   *
   * KeyData(incl keyId 2 and keySize 16) = 0x021011111111222222223333333344444455
   *
   * identificationNumber = 0x99310014
   * manufacturer = 0x8f19(FLO)
   * version = 0x50 (SMR5.0)
   * medium = 0x03
   * keyChangeCounter=1
   *
   * iv=0x140031998f15500300000001
   *
   * KeyData (4 bytes KCC, 18 bytes encrypted key (including key id and key size), 8 bytes mac):
   *
   * 0x00000001 1928dd82e493e489eed5a235E771c2232523 c60ac982f22c820a
   */

  @Test
  void testEncryptAndAddGcmAuthenticationTag() throws ProtocolAdapterException {
    final int keyId = 2;
    final int kcc = 1;

    final byte[] encryptedKeyWithMacAndKcc =
        this.macGeneration.encryptAndAddGcmAuthenticationTagSmr5(
            this.DEVICE_G, keyId, this.KEY_SIZE, kcc, this.MASTER_KEY, this.NEW_KEY);

    final byte[] withoutKcc =
        Arrays.copyOfRange(encryptedKeyWithMacAndKcc, 4, encryptedKeyWithMacAndKcc.length);

    assertThat(withoutKcc)
        .containsExactly(
            0x19, 0x28, 0xDD, 0x82, 0xE4, 0x93, 0xE4, 0x89, 0xEE, 0xD5, 0xA2, 0x35, 0xE7, 0x71,
            0xC2, 0x23, 0x25, 0x23, 0xC6, 0x0A, 0xC9, 0x82, 0xF2, 0x2C, 0x82, 0x0A);
  }

  @Test
  void testEncryptAndAddGcmAuthenticationTagWithoutKcc() throws ProtocolAdapterException {
    final int keyId = 0;
    final Integer kcc = null; // When KCC is null, then the number of seconds since 2000 are used

    final byte[] encryptedKeyWithMacAndKcc =
        this.macGeneration.encryptAndAddGcmAuthenticationTagSmr5(
            this.DEVICE_G, keyId, this.KEY_SIZE, kcc, this.MASTER_KEY, this.NEW_KEY);

    assertThat(encryptedKeyWithMacAndKcc).hasSize(this.KEY_DATA_SIZE_SMR5);

    // KCC is the number of seconds since 2000, check with a small margin
    // Note: we can't verify the encrypted key, since it will be different every time.
    final byte[] kccBytes = Arrays.copyOfRange(encryptedKeyWithMacAndKcc, 0, KCC_LENGTH);
    final long secondsSince2000 = new BigInteger(kccBytes).longValue();
    final int marginInSeconds = 5;
    assertThat(this.getSecondsSinceJanuary2000())
        .isBetween(secondsSince2000, secondsSince2000 + marginInSeconds);
  }

  @Test
  void testEncryptAndAddGcmAuthenticationTagWithWrongLengthMasterKey() {
    final int keyId = 0;
    final int kcc = 1;

    assertThrows(
        ProtocolAdapterException.class,
        () -> {
          this.macGeneration.encryptAndAddGcmAuthenticationTagSmr5(
              this.DEVICE_G, keyId, this.KEY_SIZE, kcc, new byte[] {0x00}, this.NEW_KEY);
        });
  }

  @Test
  void testCreateIv() throws ProtocolAdapterException {
    final byte[] kcc = new byte[] {0x00, 0x00, 0x01, 0x02};
    final int mBusVersion = 0x50;
    final int medium = 3;
    final byte[] initialisationVector =
        this.macGeneration.createIV(this.DEVICE_G, kcc, mBusVersion, medium);

    // Initialisation vector contains:
    // Identification number, 4 bytes, LSB first: "99310014" --> hex 14 00 31 99
    // Manufacturer id, 2 bytes, LSB first: "FLO" --> hex 8f 19
    // Version, 1 byte: hex 06
    // Medium, 1 byte: hex 03
    // KCC, 4 bytes, MSB first: 258 --> hex 00 00 01 02
    assertThat(initialisationVector)
        .containsExactly(
            0x14, 0x00, 0x31, 0x99, 0x8f, 0x19, mBusVersion, medium, 0x00, 0x00, 0x01, 0x02);
  }

  @Test
  void testCreateIvWithIdentificationNumberWithSmallNumber() throws ProtocolAdapterException {
    final byte[] kcc = new byte[] {0x00, 0x00, 0x01, 0x02};
    final int mBusVersion = 0x50;
    final int medium = 3;
    final DlmsDevice device = this.DEVICE_G;
    device.setMbusIdentificationNumber("00000561");

    final byte[] initialisationVector =
        this.macGeneration.createIV(device, kcc, mBusVersion, medium);

    // Initialisation vector contains:
    // Identification number, 4 bytes, LSB first: "00000561" --> hex 05 61 00 00
    // Manufacturer id, 2 bytes, LSB first: "FLO" --> hex 8f 19
    // Version, 1 byte: hex 06
    // Medium, 1 byte: hex 03
    // KCC, 4 bytes, MSB first: 258 --> hex 00 00 01 02
    assertThat(initialisationVector)
        .containsExactly(
            0x61, 0x05, 0x00, 0x00, 0x8f, 0x19, mBusVersion, medium, 0x00, 0x00, 0x01, 0x02);
  }

  private DlmsDevice createDlmsDevice(final Protocol protocol, final String deviceIdentification) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    device.setDeviceIdentification(deviceIdentification);
    device.setMbusManufacturerIdentification("FLO");
    device.setMbusIdentificationNumber("99310014");
    return device;
  }

  private long getSecondsSinceJanuary2000() {
    final LocalDateTime january2000 = LocalDateTime.of(2000, 1, 1, 0, 0);
    return ChronoUnit.SECONDS.between(january2000, LocalDateTime.now());
  }
}
