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
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.FirmwareFile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.core.io.ClassPathResource;

@ExtendWith(MockitoExtension.class)
public class MacGenerationServiceTest {

  @InjectMocks MacGenerationService macGenerationService;
  @Mock SecretManagementService secretManagementService;

  final byte[] firmwareUpdateAuthenticationKey = Hex.decode("F9AA0123456789012345D7AFCCD41BD1");
  final String expectedIv = "e91e40050010500300400011";
  final String expectedMac = "9a72acd7a949861cc4df4612cbdbdef6";

  private static byte[] byteArray;
  private static final int mbusDeviceIdentificationNumber = Integer.parseInt("10000540", 16);
  private static final String deviceIdentification = "G0035161000054016";
  private static final MessageMetadata messageMetadata =
      MessageMetadata.newBuilder().withCorrelationUid("123456").build();

  @BeforeAll
  public static void init() throws IOException {
    final String filename = "test-short-v00400011-snffffffff-newmods.bin";
    byteArray = Files.readAllBytes(new ClassPathResource(filename).getFile().toPath());
  }

  @Test
  void calculateMac() throws IOException, ProtocolAdapterException {

    when(this.secretManagementService.getKey(
            messageMetadata,
            deviceIdentification,
            SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION))
        .thenReturn(this.firmwareUpdateAuthenticationKey);

    final FirmwareFile firmwareFile = this.createFirmwareFile();
    final byte[] calculatedMac =
        this.macGenerationService.calculateMac(messageMetadata, deviceIdentification, firmwareFile);

    assertThat(Hex.toHexString(calculatedMac)).isEqualTo(this.expectedMac);
  }

  @Test
  void testNoKey() throws IOException, ProtocolAdapterException {
    when(this.secretManagementService.getKey(
            messageMetadata,
            deviceIdentification,
            SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION))
        .thenReturn(null);

    final FirmwareFile firmwareFile = this.createFirmwareFile();

    final Exception exception =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              this.macGenerationService.calculateMac(
                  messageMetadata, deviceIdentification, firmwareFile);
            });
    assertThat(exception)
        .hasMessageContaining(
            "No key of type G_METER_FIRMWARE_UPDATE_AUTHENTICATION found for device");
  }

  @Test
  public void testIV() throws IOException, ProtocolAdapterException {

    final FirmwareFile firmwareFile = this.createFirmwareFile();
    final byte[] iv = this.macGenerationService.createIV(firmwareFile);

    assertThat(Hex.toHexString(iv)).isEqualTo(this.expectedIv);
  }

  private FirmwareFile createFirmwareFile() throws ProtocolAdapterException {
    final FirmwareFile firmwareFile = new FirmwareFile(byteArray.clone());
    firmwareFile.setMbusDeviceIdentificationNumber(mbusDeviceIdentificationNumber);
    return firmwareFile;
  }

  @Test
  public void testInvalidFirmwareImageMagicNumber() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[0] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected FirmwareImageMagicNumber in header firmware file");
  }

  @Test
  public void testInvalidHeaderLength() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[5] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected length of header in header firmware file");
  }

  @Test
  public void testInvalidAddressLength() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[18] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected length of address in header firmware file");
  }

  @Test
  public void testInvalidAddressType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[19] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        IllegalArgumentException.class, clonedByteArray, "No AddressType found with code");
  }

  @Test
  public void testNonExistingSecurityType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[17] = (byte) 6;
    this.assertExceptionContainsMessageOnCalculateMac(
        IllegalArgumentException.class, clonedByteArray, "No SecurityType found with code");
  }

  @Test
  public void testNotExpectedSecurityType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[17] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected type of security in header firmware file");
  }

  @Test
  public void testInvalidSecurityLength() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[15] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected length of security in header firmware file");
  }

  @Test
  public void testNotExpectedActivationType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[28] = (byte) 1;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected type of activation in header firmware file");
  }

  @Test
  public void testNonExistingActivationType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[28] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        IllegalArgumentException.class, clonedByteArray, "No ActivationType found with code");
  }

  @Test
  public void testNonExistingDeviceType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[27] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        IllegalArgumentException.class, clonedByteArray, "No DeviceType found with code");
  }

  private void assertExceptionContainsMessageOnCalculateMac(
      final Class<? extends Exception> exceptionClass,
      final byte[] malformedFirmwareFile,
      final String partOfExceptionMessage)
      throws ProtocolAdapterException {

    final FirmwareFile firmwareFile = new FirmwareFile(malformedFirmwareFile);
    firmwareFile.setMbusDeviceIdentificationNumber(mbusDeviceIdentificationNumber);

    final Exception exception =
        assertThrows(
            exceptionClass,
            () -> {
              this.macGenerationService.calculateMac(
                  messageMetadata, deviceIdentification, firmwareFile);
            });
    assertThat(exception).hasMessageContaining(partOfExceptionMessage);
  }
}
