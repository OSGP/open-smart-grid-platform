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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.ActivationType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.AddressType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.DeviceType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.SecurityType;

@Slf4j
public class FirmwareFileTest {

  private FirmwareFile firmwareFile;

  @BeforeEach
  public void init() throws IOException {
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
    final byte[] byteArray = buffer.toByteArray();

    this.firmwareFile = new FirmwareFile(byteArray);
  }

  @Test
  public void testHeader() throws UnsupportedEncodingException {
    final FirmwareFileHeader header = this.firmwareFile.getHeader();
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
    assertThat(header.getManufacturerId().getIdentification()).isEqualTo("GWI");
    assertThat(header.getVersionInt()).isEqualTo(80);
    assertThat(header.getDeviceType()).isEqualTo(DeviceType.GAS);
    assertThat(header.getActivationTypeEnum())
        .isEqualTo(ActivationType.MASTER_INITIATED_ACTIVATION);
    assertThat(header.getActivationTimeHex()).isEqualTo("000000000000");
  }

  @Test
  public void testMbusDeviceSerialNumber() throws IOException {
    final String idHex = "40050010";
    final int mbusDeviceSerialNumber = Integer.decode("0x" + idHex);
    this.firmwareFile.setMbusDeviceSerialNumber(mbusDeviceSerialNumber);
    log.debug(this.firmwareFile.getHeader().toString());
    assertThat(this.firmwareFile.getHeader().getMbusDeviceSerialNumber()).isEqualTo(idHex);
  }
}
