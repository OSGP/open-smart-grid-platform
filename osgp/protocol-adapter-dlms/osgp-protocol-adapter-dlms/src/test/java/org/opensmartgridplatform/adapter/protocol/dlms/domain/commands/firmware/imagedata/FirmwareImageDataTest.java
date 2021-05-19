/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.imagedata;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class FirmwareImageDataTest {

  private FirmwareImageData firmwareImageData;

  @BeforeEach
  public void init() throws IOException {
    final String filename = "integra-v00400011-snffffffff-newmods.bin";
    final InputStream resourceAsStream =
        FirmwareImageDataTest.class.getClassLoader().getResourceAsStream(filename);

    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int nRead;
    final byte[] data = new byte[1024];
    while ((nRead = resourceAsStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }

    buffer.flush();
    final byte[] byteArray = buffer.toByteArray();

    this.firmwareImageData = new FirmwareImageData(byteArray);
  }

  @Test
  public void testHeader() throws UnsupportedEncodingException {
    final FirmwareImageDataHeader header = this.firmwareImageData.getHeader();
    log.debug(header.toString());
    assertThat(header.getFirmwareImageMagicNumberHex()).isEqualTo("534d5235");
    assertThat(header.getHeaderVersionInt()).isEqualTo(0);
    assertThat(header.getHeaderLengthHex()).isEqualTo("2300");
    assertThat(header.getFirmwareImageVersionHex()).isEqualTo("11004000");
    assertThat(header.getFirmwareImageLengthHex()).isEqualTo("b0760200");
    assertThat(header.getSecurityLengthInt()).isEqualTo(16);
    assertThat(header.getSecurityTypeInt()).isEqualTo(2);
    assertThat(header.getAddressLengthInt()).isEqualTo(8);
    assertThat(header.getAddressTypeInt()).isEqualTo(1);
    assertThat(header.getIdentificationNumber()).isEqualTo("ffffffff");
    assertThat(header.getMftHex()).isEqualTo("e91e");
    assertThat(header.getVersionInt()).isEqualTo(80);
    assertThat(header.getTypeInt()).isEqualTo(3);
    assertThat(header.getActivationTypeInt()).isEqualTo(3);
    assertThat(header.getActivationTimeHex()).isEqualTo("000000000000");
  }

  @Test
  public void testIdentrificationNumber() throws IOException {
    final String idHex = "40050010";
    final Long identificationNumber = Long.decode("0x" + idHex);
    this.firmwareImageData.addIdentificationNumber(identificationNumber);
    log.debug(this.firmwareImageData.getHeader().toString());
    assertThat(this.firmwareImageData.getHeader().getIdentificationNumber()).isEqualTo(idHex);
  }
}
