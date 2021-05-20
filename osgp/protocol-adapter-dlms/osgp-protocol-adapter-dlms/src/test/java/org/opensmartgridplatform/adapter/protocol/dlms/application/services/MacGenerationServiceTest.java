/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.FirmwareFile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.FirmwareFileTest;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class MacGenerationServiceTest {

  @InjectMocks MacGenerationService macGenerationService;
  @Mock SecretManagementService secretManagementService;

  // FIRMWARE UPDATE AUTHENTICATION KEY
  final byte[] authenticationKey = Hex.decode("F9AA9442108723357221D7AFCCD41BD1");
  final String expectedIv = "e91e40050010500300400011";
  final String expectedMac = "b4375a6b43de6d2421628bba7d6ee0e6";

  private static byte[] byteArray;
  private static final int mbusDeviceSerialNumber = Integer.decode("0x40050010");
  private final String deviceIdentification = "G0035161000054016";

  @BeforeAll
  public static void init() throws IOException {
    final String filename = "integra-v00400011-snffffffff-newmods.bin";
    final InputStream resourceAsStream =
        FirmwareFileTest.class.getClassLoader().getResourceAsStream(filename);

    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int nRead;
    final byte[] data = new byte[1024];
    while ((nRead = resourceAsStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }

    buffer.flush();
    byteArray = buffer.toByteArray();
  }

  @Test
  void calculateMac() throws IOException, ProtocolAdapterException {
    // GIVEN
    when(this.secretManagementService.getKey(
            eq(this.deviceIdentification),
            eq(SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION)))
        .thenReturn(this.authenticationKey);
    // WHEN
    final FirmwareFile firmwareFile = new FirmwareFile(byteArray);
    firmwareFile.setMbusDeviceSerialNumber(mbusDeviceSerialNumber);
    final byte[] calculatedMac =
        this.macGenerationService.calculateMac(this.deviceIdentification, firmwareFile);
    // THEN
    assertThat(Hex.toHexString(calculatedMac)).isEqualTo(this.expectedMac);
  }

  @Test
  void testNoKey() throws IOException, ProtocolAdapterException {
    // GIVEN
    when(this.secretManagementService.getKey(
            eq(this.deviceIdentification),
            eq(SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION)))
        .thenReturn(null);
    // WHEN
    final FirmwareFile firmwareFile = new FirmwareFile(byteArray);
    firmwareFile.setMbusDeviceSerialNumber(mbusDeviceSerialNumber);
    // THEN
    final Exception exception =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              this.macGenerationService.calculateMac(this.deviceIdentification, firmwareFile);
            });
    assertThat(exception.getMessage())
        .contains("No key of type G_METER_FIRMWARE_UPDATE_AUTHENTICATION found for device");
  }

  @Test
  public void testIV() throws IOException {
    // WHEN
    final FirmwareFile firmwareFile = new FirmwareFile(byteArray);
    firmwareFile.setMbusDeviceSerialNumber(mbusDeviceSerialNumber);
    final byte[] iv = this.macGenerationService.createIV(firmwareFile);
    // THEN
    assertThat(Hex.toHexString(iv)).isEqualTo(this.expectedIv);
  }

  @Test
  public void testInvalidFirmwareImageMagicNumber() throws IOException, ProtocolAdapterException {
    // WHEN
    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[0] = (byte) 0;
    this.assertException(clonedByteArray, "Unexpected FirmwareImageMagicNumber in header FW file");
  }

  @Test
  public void testInvalidHeaderVersion() throws IOException, ProtocolAdapterException {
    // WHEN
    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[4] = (byte) 1;
    this.assertException(clonedByteArray, "Unexpected HeaderVersion in header FW file");
  }

  @Test
  public void testInvalidHeaderLength() throws IOException, ProtocolAdapterException {
    // WHEN
    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[5] = (byte) 0;
    this.assertException(clonedByteArray, "Unexpected length of header in header FW file");
  }

  @Test
  public void testInvalidAddressLength() throws IOException, ProtocolAdapterException {
    // WHEN
    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[18] = (byte) 0;
    this.assertException(clonedByteArray, "Unexpected length of address in header FW file");
  }

  @Test
  public void testInvalidAddressType() throws IOException, ProtocolAdapterException {
    // WHEN
    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[19] = (byte) 0;
    this.assertException(clonedByteArray, "Unexpected type of address in header FW file");
  }

  @Test
  public void testInvalidSecurityType() throws IOException, ProtocolAdapterException {
    // WHEN
    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[17] = (byte) 0;
    this.assertException(clonedByteArray, "Unexpected type of security in header FW file");
  }

  @Test
  public void testInvalidSecurityLength() throws IOException, ProtocolAdapterException {
    // WHEN
    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[15] = (byte) 0;
    this.assertException(clonedByteArray, "Unexpected length of security in header FW file");
  }

  @Test
  public void testInvalidActivationType() throws IOException, ProtocolAdapterException {
    // WHEN
    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[28] = (byte) 0;
    this.assertException(clonedByteArray, "Unexpected type of activation in header FW file");
  }

  @Test
  public void testInvalidDeviceType() throws IOException, ProtocolAdapterException {
    // WHEN
    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[27] = (byte) 0;
    this.assertException(clonedByteArray, "Unexpected type of device in header FW file");
  }

  private void assertException(final byte[] clonedByteArray, final String message) {
    // WHEN
    final FirmwareFile firmwareFile = new FirmwareFile(clonedByteArray);
    log.info(firmwareFile.getHeader().toString());
    firmwareFile.setMbusDeviceSerialNumber(mbusDeviceSerialNumber);
    // THEN
    final Exception exception =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              this.macGenerationService.calculateMac(this.deviceIdentification, firmwareFile);
            });
    assertThat(exception.getMessage()).contains(message);
  }
}
