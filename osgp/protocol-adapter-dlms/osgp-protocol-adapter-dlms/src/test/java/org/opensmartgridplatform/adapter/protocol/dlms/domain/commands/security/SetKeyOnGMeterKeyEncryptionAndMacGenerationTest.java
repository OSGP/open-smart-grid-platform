/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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

  private final byte[] MASTER_KEY =
      new byte[] {
        (byte) 0xfd,
        (byte) 0xd0,
        (byte) 0xfd,
        (byte) 0xd0,
        (byte) 0xfd,
        (byte) 0xd0,
        (byte) 0xfd,
        (byte) 0xd0,
        (byte) 0xfd,
        (byte) 0xd0,
        (byte) 0xfd,
        (byte) 0xd0,
        (byte) 0xfd,
        (byte) 0xd0,
        (byte) 0xfd,
        (byte) 0xd0
      };
  private final byte[] NEW_USER_KEY =
      new byte[] {
        (byte) 0xfa,
        (byte) 0xa0,
        (byte) 0xfe,
        (byte) 0xa0,
        (byte) 0xfe,
        (byte) 0xa0,
        (byte) 0xfe,
        (byte) 0xa0,
        (byte) 0xfe,
        (byte) 0xa0,
        (byte) 0xfe,
        (byte) 0xa0,
        (byte) 0xfe,
        (byte) 0xa0,
        (byte) 0xfe,
        (byte) 0xa0
      };

  private final SetKeyOnGMeterKeyEncryptionAndMacGeneration macGeneration =
      new SetKeyOnGMeterKeyEncryptionAndMacGeneration();

  @Test
  void testEncryptDsmr4() throws ProtocolAdapterException {
    final byte[] encryptedKey =
        this.macGeneration.encryptMbusUserKeyDsmr4(this.MASTER_KEY, this.NEW_USER_KEY);

    assertThat(encryptedKey)
        .containsExactly(
            0x7b, 0x33, 0x83, 0xe2, 0xc3, 0xec, 0x93, 0x60, 0xd5, 0x4a, 0x05, 0xd0, 0x55, 0x17,
            0x73, 0x8b);
  }

  /* Verified example for SMR5 user key:
  * MASTER_KEY = 0xfdd0fdd0fdd0fdd0fdd0fdd0fdd0fdd0
  * NEW_USER_KEY = 0xfaa0fea0fea0fea0fea0fea0fea0fea0
  *
  * KeyData(incl keyId 0 and keySize 16) = 0x0010faa0fea0fea0fea0fea0fea0fea0fea0
  *
  * identificationNumber = 0x12345678(12345678)
  * manufacturer = 0x8d3a(NTM)
  * version = 0x06
  * medium = 0x03
  * keyChangeCounter=1
  *
  * iv=0x785634123a8d060300000001

  * KeyData(encrypted, including 64 bit mac): 0xd9ab7dab07ef7c5e00aa66fcd4327e4b890c6eb17e0f90518c59
  */

  @Test
  void testEncryptAndAddGcmAuthenticationTag() throws ProtocolAdapterException {
    final int keyId = 0;
    final int keySize = 16;
    final int kcc = 1;

    final byte[] encryptedKeyWithMac =
        this.macGeneration.encryptAndAddGcmAuthenticationTagSmr5(
            this.DEVICE_G, keyId, keySize, kcc, this.MASTER_KEY, this.NEW_USER_KEY);

    assertThat(encryptedKeyWithMac)
        .containsExactly(
            0xd9, 0xab, 0x7d, 0xab, 0x07, 0xef, 0x7c, 0x5e, 0x00, 0xaa, 0x66, 0xfc, 0xd4, 0x32,
            0x7e, 0x4b, 0x89, 0x0c, 0x6e, 0xb1, 0x7e, 0x0f, 0x90, 0x51, 0x8c, 0x59);
  }

  @Test
  void testEncryptAndAddGcmAuthenticationTagWithWrongLengthKey() {
    final int keyId = 0;
    final int keySize = 16;
    final int kcc = 1;

    assertThrows(
        ProtocolAdapterException.class,
        () -> {
          this.macGeneration.encryptAndAddGcmAuthenticationTagSmr5(
              this.DEVICE_G, keyId, keySize, kcc, new byte[] {0x00}, this.NEW_USER_KEY);
        });
  }

  @Test
  void testCreateIvWithoutKcc() throws ProtocolAdapterException {
    final int mBusVersion = 6;
    final int medium = 3;
    final byte[] initialisationVector =
        this.macGeneration.createIV(this.DEVICE_G, null, mBusVersion, medium);

    // Initialisation vector contains:
    // Identification number, 4 bytes, LSB first: "12345678" --> hex 78 56 34 12
    // Manufacturer id, 2 bytes, LSB first: "NTM" --> hex 3a 8d
    // Version, 1 byte: hex 06
    // Medium, 1 byte: hex 03
    // KCC, 4 bytes, MSB first: 258 --> hex 00 00 01 02
    assertThat(initialisationVector).hasSize(12);
    final byte[] ivExcludingKcc = Arrays.copyOfRange(initialisationVector, 0, 8);
    assertThat(ivExcludingKcc)
        .containsExactly(0x78, 0x56, 0x34, 0x12, 0x3a, 0x8d, mBusVersion, medium);

    final byte[] kcc = Arrays.copyOfRange(initialisationVector, 8, 12);
    final long kccValue = new BigInteger(kcc).longValue();
    final int marginInSeconds = 5;
    assertThat(this.getSecondsSinceJanuary2000()).isBetween(kccValue, kccValue + marginInSeconds);
  }

  @Test
  void testCreateIvWithKcc() throws ProtocolAdapterException {
    final int kcc = 258; // Value 258 translates to hex 00 00 01 02
    final int mBusVersion = 5;
    final int medium = 2;
    final byte[] initialisationVector =
        this.macGeneration.createIV(this.DEVICE_G, kcc, mBusVersion, medium);

    // Initialisation vector contains:
    // Identification number, 4 bytes, LSB first: "12345678" --> hex 78 56 34 12
    // Manufacturer id, 2 bytes, LSB first: "NTM" --> hex 3a 8d
    // Version, 1 byte: hex 05
    // Medium, 1 byte: hex 02
    // KCC, 4 bytes, MSB first: 258 --> hex 00 00 01 02
    assertThat(initialisationVector)
        .containsExactly(
            0x78, 0x56, 0x34, 0x12, 0x3a, 0x8d, mBusVersion, medium, 0x00, 0x00, 0x01, 0x02);
  }

  private DlmsDevice createDlmsDevice(final Protocol protocol, final String deviceIdentification) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    device.setDeviceIdentification(deviceIdentification);
    device.setMbusManufacturerIdentification("NTM");
    device.setMbusIdentificationNumber("12345678");
    return device;
  }

  private long getSecondsSinceJanuary2000() {
    final LocalDateTime january2000 = LocalDateTime.of(2000, 1, 1, 0, 0);
    return ChronoUnit.SECONDS.between(january2000, LocalDateTime.now());
  }
}
