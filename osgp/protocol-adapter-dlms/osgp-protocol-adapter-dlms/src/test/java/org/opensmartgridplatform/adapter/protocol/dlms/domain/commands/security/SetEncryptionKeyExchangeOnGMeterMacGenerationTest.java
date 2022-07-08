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
class SetEncryptionKeyExchangeOnGMeterMacGenerationTest {

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
  private final byte[] USER_KEY =
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

  private final SetEncryptionKeyExchangeOnGMeterMacGeneration macGeneration =
      new SetEncryptionKeyExchangeOnGMeterMacGeneration();

  @Test
  void testCreateMac() throws ProtocolAdapterException {
    final byte[] generatedMac =
        this.macGeneration.calculateMac(this.DEVICE_G, 0, 16, 1, this.USER_KEY, this.MASTER_KEY);

    assertThat(generatedMac).containsExactly(0x6e, 0xb1, 0x7e, 0x0f, 0x90, 0x51, 0x8c, 0x59);
  }

  @Test
  void testCreateIvWithoutKcc() throws ProtocolAdapterException {
    final byte[] initialisationVector = this.macGeneration.createIV(this.DEVICE_G, null);

    assertThat(initialisationVector).hasSize(12);
    final byte[] ivExcludingKcc = Arrays.copyOfRange(initialisationVector, 0, 8);
    assertThat(ivExcludingKcc).containsExactly(0x78, 0x56, 0x34, 0x12, 0x3a, 0x8d, 0x06, 0x03);
    final byte[] kcc = Arrays.copyOfRange(initialisationVector, 8, 12);
    final long kccValue = new BigInteger(kcc).longValue();
    assertThat(this.getSecondsSinceJanuary2000()).isBetween(kccValue, kccValue + 5);
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
