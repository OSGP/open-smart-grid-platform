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

  private static byte[] byteArray;
  private static final String filename = "integra-v00400011-snffffffff-newmods.bin";

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
    assertThat(header.getFirmwareImageLengthInt()).isEqualTo(161456);
    assertThat(header.getSecurityLengthInt()).isEqualTo(16);
    assertThat(header.getSecurityTypeEnum()).isEqualTo(SecurityType.GMAC);
    assertThat(header.getAddressLengthInt()).isEqualTo(8);
    assertThat(header.getAddressTypeEnum()).isEqualTo(AddressType.MBUS_ADDRESS);
    assertThat(header.getMbusDeviceSerialNumber()).isEqualTo("ffffffff");
    assertThat(header.getMbusManufacturerId().getIdentification()).isEqualTo("GWI");
    assertThat(header.getMbusVersionInt()).isEqualTo(80);
    assertThat(header.getMbusDeviceType()).isEqualTo(DeviceType.GAS);
    assertThat(header.getActivationTypeEnum())
        .isEqualTo(ActivationType.MASTER_INITIATED_ACTIVATION);
    assertThat(header.getActivationTimeHex()).isEqualTo("000000000000");
  }

  @Test
  public void testFittingMbusDeviceSerialNumber() throws IOException, ProtocolAdapterException {
    final FirmwareFile firmwareFile = this.createPartialWildcardFirmwareFile();

    final String idHex = "0000FFFF";
    final int noncompliantMbusDeviceSerialNumberInt = Integer.parseInt(idHex, 16);

    assertDoesNotThrow(
        () -> firmwareFile.setMbusDeviceSerialNumber(noncompliantMbusDeviceSerialNumberInt));
  }

  @Test
  public void testMisfitMbusDeviceSerialNumber() throws IOException, ProtocolAdapterException {
    final FirmwareFile firmwareFile = this.createPartialWildcardFirmwareFile();

    final String idHex = "00010000";
    final int noncompliantMbusDeviceSerialNumberInt = Integer.parseInt(idHex, 16);
    final Exception exception =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              firmwareFile.setMbusDeviceSerialNumber(noncompliantMbusDeviceSerialNumberInt);
            });
    assertThat(exception)
        .hasMessage(
            "MbusDevice Serial Number (%s) does not fit the range of serial numbers supported by this Firmware File (%s)",
            idHex,
            new StringBuffer(firmwareFile.getHeader().getMbusDeviceSerialNumber())
                .reverse()
                .toString());
  }

  private FirmwareFile createPartialWildcardFirmwareFile() {
    // Changed fully wildcarded (FFFFFFFF) MbusDeviceSerialNumber in header with partially
    // wildcarded one (FFFF0000, LSB first)
    // So meters with IDs 00000001 to 00009999 [decimal] do fit
    final byte[] clonedByteArray = byteArray.clone();
    clonedByteArray[22] = (byte) 255;
    clonedByteArray[23] = (byte) 255;
    clonedByteArray[24] = (byte) 0;
    clonedByteArray[25] = (byte) 0;

    final FirmwareFile firmwareFile = new FirmwareFile(clonedByteArray);
    return firmwareFile;
  }

  @Test
  public void testMbusDeviceSerialNumber() throws IOException, ProtocolAdapterException {
    final String idHex = "10000540";
    final int mbusDeviceSerialNumberInput = Integer.parseInt(idHex, 16);
    final byte[] mbusDeviceSerialNumberByteArrayOutput =
        ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(mbusDeviceSerialNumberInput)
            .array();

    final FirmwareFile firmwareFile = new FirmwareFile(byteArray);
    firmwareFile.setMbusDeviceSerialNumber(mbusDeviceSerialNumberInput);
    log.debug(firmwareFile.getHeader().toString());

    assertThat(
            firmwareFile
                .getHeader()
                .getFirmwareFileHeaderAddressField()
                .getMbusDeviceSerialNumber())
        .isEqualTo(mbusDeviceSerialNumberByteArrayOutput);
  }
}
