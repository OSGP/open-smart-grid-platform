// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

  static final String fwFilename1 = "test-short-v00400011-snffffffff-newmods.bin";

  final byte[] firmwareUpdateAuthenticationKey1 = Hex.decode("F9AA0123456789012345D7AFCCD41BD1");
  private final String deviceIdentification1 = "G0035161000054016";

  final String expectedIv1 = "e91e40050010500300400011";
  final String expectedMac1 = "9a72acd7a949861cc4df4612cbdbdef6";

  private static final MessageMetadata messageMetadata =
      MessageMetadata.newBuilder().withCorrelationUid("123456").build();

  private static byte[] byteArray1;
  private static byte[] byteArray2;

  @BeforeAll
  public static void init() throws IOException, ProtocolAdapterException {
    byteArray1 = Files.readAllBytes(new ClassPathResource(fwFilename1).getFile().toPath());
    // Same firmware file but now with wildcarded mbusversion (Florian firmware)
    final FirmwareFile firmwareFile2 = new FirmwareFile(byteArray1);
    firmwareFile2.setMbusVersion(255);
    byteArray2 = firmwareFile2.getByteArray();
  }

  @Test
  void calculateMac1() throws IOException, ProtocolAdapterException {
    this.calculateMac(
        byteArray1,
        this.deviceIdentification1,
        this.firmwareUpdateAuthenticationKey1,
        this.expectedMac1);
  }

  @Test
  void calculateMac2() throws IOException, ProtocolAdapterException {

    this.calculateMac(
        byteArray2,
        this.deviceIdentification1,
        this.firmwareUpdateAuthenticationKey1,
        this.expectedMac1);
  }

  void calculateMac(
      final byte[] byteArray,
      final String deviceIdentification,
      final byte[] firmwareUpdateAuthenticationKey,
      final String expectedMac)
      throws IOException, ProtocolAdapterException {

    when(this.secretManagementService.getKey(
            messageMetadata,
            deviceIdentification,
            SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION))
        .thenReturn(firmwareUpdateAuthenticationKey);

    final String mbusDeviceIdentificationNumber = deviceIdentification.substring(7, 15);

    final FirmwareFile firmwareFile =
        this.createFirmwareFile(byteArray, mbusDeviceIdentificationNumber);

    final byte[] calculatedMac =
        this.macGenerationService.calculateMac(messageMetadata, deviceIdentification, firmwareFile);

    assertThat(Hex.toHexString(calculatedMac)).isEqualTo(expectedMac);
  }

  @Test
  void testNoKey() throws IOException, ProtocolAdapterException {
    when(this.secretManagementService.getKey(
            messageMetadata,
            this.deviceIdentification1,
            SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION))
        .thenReturn(null);

    final FirmwareFile firmwareFile =
        this.createFirmwareFile(byteArray1, this.deviceIdentification1.substring(7, 15));

    final Exception exception =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              this.macGenerationService.calculateMac(
                  messageMetadata, this.deviceIdentification1, firmwareFile);
            });
    assertThat(exception)
        .hasMessageContaining(
            "No key of type G_METER_FIRMWARE_UPDATE_AUTHENTICATION found for device");
  }

  @Test
  public void testIV1() throws IOException, ProtocolAdapterException {
    this.testIV(byteArray1, this.expectedIv1, this.deviceIdentification1.substring(7, 15));
  }

  @Test
  public void testIV2() throws IOException, ProtocolAdapterException {
    this.testIV(byteArray2, this.expectedIv1, this.deviceIdentification1.substring(7, 15));
  }

  public void testIV(
      final byte[] byteArray, final String expectedIv, final String mbusDeviceIdentificationNumber)
      throws IOException, ProtocolAdapterException {

    final FirmwareFile firmwareFile =
        this.createFirmwareFile(byteArray, mbusDeviceIdentificationNumber);

    final byte[] iv = this.macGenerationService.createIV(firmwareFile);

    assertThat(Hex.toHexString(iv)).isEqualTo(expectedIv);
  }

  private FirmwareFile createFirmwareFile(
      final byte[] byteArray, final String mbusDeviceIdentificationNumber)
      throws ProtocolAdapterException {
    final FirmwareFile firmwareFile = new FirmwareFile(byteArray.clone());
    System.out.println(firmwareFile.getHeader());
    firmwareFile.setMbusDeviceIdentificationNumber(mbusDeviceIdentificationNumber);
    firmwareFile.setMbusVersion(80);
    System.out.println(firmwareFile.getHeader());
    return firmwareFile;
  }

  @Test
  public void testInvalidFirmwareImageMagicNumber() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray1.clone();
    clonedByteArray[0] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected FirmwareImageMagicNumber in header firmware file",
        this.deviceIdentification1);
  }

  @Test
  public void testInvalidHeaderLength() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray1.clone();
    clonedByteArray[5] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected length of header in header firmware file",
        this.deviceIdentification1);
  }

  @Test
  public void testInvalidAddressLength() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray1.clone();
    clonedByteArray[18] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected length of address in header firmware file",
        this.deviceIdentification1);
  }

  @Test
  public void testInvalidAddressType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray1.clone();
    clonedByteArray[19] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        IllegalArgumentException.class,
        clonedByteArray,
        "No AddressType found with code",
        this.deviceIdentification1);
  }

  @Test
  public void testNonExistingSecurityType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray1.clone();
    clonedByteArray[17] = (byte) 6;
    this.assertExceptionContainsMessageOnCalculateMac(
        IllegalArgumentException.class,
        clonedByteArray,
        "No SecurityType found with code",
        this.deviceIdentification1);
  }

  @Test
  public void testNotExpectedSecurityType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray1.clone();
    clonedByteArray[17] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected type of security in header firmware file",
        this.deviceIdentification1);
  }

  @Test
  public void testInvalidSecurityLength() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray1.clone();
    clonedByteArray[15] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected length of security in header firmware file",
        this.deviceIdentification1);
  }

  @Test
  public void testNotExpectedActivationType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray1.clone();
    clonedByteArray[28] = (byte) 1;
    this.assertExceptionContainsMessageOnCalculateMac(
        ProtocolAdapterException.class,
        clonedByteArray,
        "Unexpected type of activation in header firmware file",
        this.deviceIdentification1);
  }

  @Test
  public void testNonExistingActivationType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray1.clone();
    clonedByteArray[28] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        IllegalArgumentException.class,
        clonedByteArray,
        "No ActivationType found with code",
        this.deviceIdentification1);
  }

  @Test
  public void testNonExistingDeviceType() throws IOException, ProtocolAdapterException {

    final byte[] clonedByteArray = byteArray1.clone();
    clonedByteArray[27] = (byte) 0;
    this.assertExceptionContainsMessageOnCalculateMac(
        IllegalArgumentException.class,
        clonedByteArray,
        "No DeviceType found with code",
        this.deviceIdentification1);
  }

  private void assertExceptionContainsMessageOnCalculateMac(
      final Class<? extends Exception> exceptionClass,
      final byte[] malformedFirmwareFile,
      final String partOfExceptionMessage,
      final String deviceIdentification)
      throws ProtocolAdapterException {

    final FirmwareFile firmwareFile = new FirmwareFile(malformedFirmwareFile);
    firmwareFile.setMbusDeviceIdentificationNumber(deviceIdentification.substring(7, 15));

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
