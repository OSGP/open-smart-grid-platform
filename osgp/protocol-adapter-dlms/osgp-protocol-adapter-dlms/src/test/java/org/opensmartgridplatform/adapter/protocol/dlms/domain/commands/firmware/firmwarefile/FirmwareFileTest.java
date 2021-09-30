/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.ActivationType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.AddressType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.DeviceType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.SecurityType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class FirmwareFileTest {

  private static final String filename = "test-short-v00400011-snffffffff-newmods.bin";

  private static byte[] byteArray;

  @BeforeEach
  public void init() throws IOException {
    byteArray = Files.readAllBytes(new ClassPathResource(filename).getFile().toPath());
  }

  @Test
  public void testHeader() throws UnsupportedEncodingException {
    final FirmwareFile firmwareFile = new FirmwareFile(byteArray);
    final FirmwareFileHeader header = firmwareFile.getHeader();
    log.debug(header.toString());
    assertThat(header.getFirmwareImageMagicNumberHex())
        .isEqualTo(FirmwareFile.FIRMWARE_IMAGE_MAGIC_NUMBER);
    assertThat(header.getHeaderVersionInt()).isEqualTo(0);
    assertThat(header.getHeaderLengthInt()).isEqualTo(35);
    assertThat(header.getFirmwareImageVersionHex()).isEqualTo("11004000");
    assertThat(header.getFirmwareImageLengthInt()).isEqualTo(1000 - (35 + 16));
    assertThat(header.getSecurityLengthInt()).isEqualTo(16);
    assertThat(header.getSecurityTypeEnum()).isEqualTo(SecurityType.GMAC);
    assertThat(header.getAddressLengthInt()).isEqualTo(8);
    assertThat(header.getAddressTypeEnum()).isEqualTo(AddressType.MBUS_ADDRESS);
    assertThat(header.getMbusDeviceIdentificationNumber()).isEqualTo("ffffffff");
    assertThat(header.getMbusManufacturerId().getIdentification()).isEqualTo("GWI");
    assertThat(header.getMbusVersionInt()).isEqualTo(80);
    assertThat(header.getMbusDeviceType()).isEqualTo(DeviceType.GAS);
    assertThat(header.getActivationTypeEnum())
        .isEqualTo(ActivationType.MASTER_INITIATED_ACTIVATION);
    assertThat(header.getActivationTimeHex()).isEqualTo("000000000000");
  }

  @Test
  public void testFittingMbusDeviceIdentificationNumber()
      throws IOException, ProtocolAdapterException {

    final FirmwareFile firmwareFile = this.createPartialWildcardFirmwareFile("FFFF0000");

    final int fittingMbusDeviceIdentificationNumberInt = Integer.parseInt("00009999");

    assertDoesNotThrow(
        () ->
            firmwareFile.setMbusDeviceIdentificationNumber(
                fittingMbusDeviceIdentificationNumberInt));
  }

  @Test
  public void testFittingMbusDeviceIdentificationNumberWithFullWildcard()
      throws IOException, ProtocolAdapterException {

    final FirmwareFile firmwareFile = this.createPartialWildcardFirmwareFile("FFFFFFFF");

    final int fittingMbusDeviceIdentificationNumberInt = Integer.parseInt("16019864");

    assertDoesNotThrow(
        () ->
            firmwareFile.setMbusDeviceIdentificationNumber(
                fittingMbusDeviceIdentificationNumberInt));
  }

  @Test
  public void testMisfitMbusDeviceIdentificationNumber()
      throws IOException, ProtocolAdapterException {
    final FirmwareFile firmwareFile = this.createPartialWildcardFirmwareFile("FFFF0000");
    final String reversedWildcard = "0000FFFF";
    final String identificationNumber = "00010000";

    final int misfittingMbusDeviceIdentificationNumberInt = Integer.parseInt(identificationNumber);
    final Exception exception =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              firmwareFile.setMbusDeviceIdentificationNumber(
                  misfittingMbusDeviceIdentificationNumberInt);
            });
    assertThat(exception)
        .hasMessage(
            "M-Bus Device Identification Number (%s) does not fit the range of Identification Numbers supported by this Firmware File (%s)",
            identificationNumber, reversedWildcard.toLowerCase());
  }

  @Test
  void acceptIdentificationNumberThatDoesNotMatchLessRegularPattern() {
    final FirmwareFile firmwareFile1 = this.createPartialWildcardFirmwareFile("FFFF0116");
    final String id = "16019864";
    final int fittingMbusIdentificationNumberInt = Integer.parseInt(id);

    assertDoesNotThrow(
        () -> firmwareFile1.setMbusDeviceIdentificationNumber(fittingMbusIdentificationNumberInt));
  }

  @Test
  void rejectIdentificationNumberThatDoesNotMatchLessRegularPattern() {
    final FirmwareFile firmwareFile = this.createPartialWildcardFirmwareFile("FFFF5922");
    final String reversedWildcard = "2259FFFF";

    final String id = "16019864";
    final int misfittingMbusIdentificationNumberInt = Integer.parseInt(id);
    final Exception exception =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              firmwareFile.setMbusDeviceIdentificationNumber(misfittingMbusIdentificationNumberInt);
            });
    assertThat(exception)
        .hasMessage(
            "M-Bus Device Identification Number (%s) does not fit the range of Identification Numbers supported by this Firmware File (%s)",
            id, reversedWildcard.toLowerCase());
  }

  private FirmwareFile createPartialWildcardFirmwareFile(final String hexWildcard) {
    final byte[] clonedByteArray = byteArray.clone();
    final byte[] byteArray = new BigInteger(hexWildcard.toLowerCase(), 16).toByteArray();
    clonedByteArray[22] = byteArray[1];
    clonedByteArray[23] = byteArray[2];
    clonedByteArray[24] = byteArray[3];
    clonedByteArray[25] = byteArray[4];
    final FirmwareFile firmwareFile = new FirmwareFile(clonedByteArray);
    return firmwareFile;
  }

  @Test
  public void testMbusDeviceIdentificationNumber() throws IOException, ProtocolAdapterException {
    final String id = "10000540";
    final int mbusDeviceIdentificationNumberInput = Integer.parseInt(id);
    final byte[] mbusDeviceIdentificationNumberByteArrayOutput =
        ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(Integer.parseInt(String.valueOf(mbusDeviceIdentificationNumberInput), 16))
            .array();

    final FirmwareFile firmwareFile = new FirmwareFile(byteArray);
    firmwareFile.setMbusDeviceIdentificationNumber(mbusDeviceIdentificationNumberInput);
    log.debug(firmwareFile.getHeader().toString());

    assertThat(
            firmwareFile
                .getHeader()
                .getFirmwareFileHeaderAddressField()
                .getMbusDeviceIdentificationNumber())
        .isEqualTo(mbusDeviceIdentificationNumberByteArrayOutput);
  }

  @Test
  public void testImageIdentifierForMbusDevice() throws ProtocolAdapterException {
    final FirmwareFile firmwareFile = new FirmwareFile(byteArray);
    firmwareFile.setMbusDeviceIdentificationNumber(Integer.parseInt("16019864"));

    assertThat(firmwareFile.createImageIdentifierForMbusDevice())
        .isEqualTo(
            new byte[] {71, 87, 73, 77, 66, 85, 83, 100, -104, 1, 22, -23, 30, 80, 3, 0, 0, 0, 0});
  }
}
