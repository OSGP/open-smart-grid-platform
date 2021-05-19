/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.nio.ByteBuffer;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.imagedata.FirmwareImageData;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.imagedata.FirmwareImageDataHeader;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.imagedata.FirmwareImageDataHeaderAddressField;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MacGenerationService {

  private static final String FIRMWARE_IMAGE_MAGIC_NUMBER = "534d5235"; // SMR5
  private static final int HEADER_LENGTH = 35;
  private static final int ADDRESS_LENGTH = 8;
  private static final int ADDRESS_TYPE = 1; // M-Bus address
  private static final int SECURITY_LENGTH = 16;
  private static final int SECURITY_TYPE = 2; // GMAC

  @Autowired private SecretManagementService secretManagementService;

  public MacGenerationService() {}

  public byte[] addMac(final String deviceIdentification, final FirmwareImageData firmwareImageData)
      throws ProtocolAdapterException {

    final byte[] calculatedMac = this.calculateMac(deviceIdentification, firmwareImageData);

    return ByteBuffer.allocate(firmwareImageData.length())
        .put(firmwareImageData.getHeaderByteArray())
        .put(firmwareImageData.getFirmwareImageByteArray())
        .put(calculatedMac)
        .array();
  }

  public byte[] calculateMac(
      final String deviceIdentification, final FirmwareImageData firmwareImageData)
      throws ProtocolAdapterException {

    this.validateHeader(firmwareImageData.getHeader());

    final byte[] iv = this.createIV(firmwareImageData);

    final byte[] decryptedFirmwareUpdateAuthenticationKey =
        this.secretManagementService.getKey(
            deviceIdentification, SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION);

    final CipherParameters cipherParameters =
        new KeyParameter(decryptedFirmwareUpdateAuthenticationKey);
    final ParametersWithIV parameterWithIV = new ParametersWithIV(cipherParameters, iv);

    final GMac mac = new GMac(new GCMBlockCipher(new AESEngine()), 128);

    mac.init(parameterWithIV);

    final byte[] headerByteArray = firmwareImageData.getHeaderByteArray();
    final byte[] firmwareImageByteArray = firmwareImageData.getFirmwareImageByteArray();
    final byte[] input =
        ByteBuffer.allocate(headerByteArray.length + firmwareImageByteArray.length)
            .put(headerByteArray)
            .put(firmwareImageByteArray)
            .array();

    mac.update(input, 0, input.length);
    final byte[] generatedMac = new byte[mac.getMacSize()];
    mac.doFinal(generatedMac, 0);

    if (firmwareImageData.getHeader().getSecurityLengthInt() != generatedMac.length) {
      throw new ProtocolAdapterException(
          "Unable to generate correct MAC: Defined security length in firmware header differs from length of generated MAC");
    }
    return generatedMac;
  }

  private void validateHeader(final FirmwareImageDataHeader header)
      throws ProtocolAdapterException {
    if (!FIRMWARE_IMAGE_MAGIC_NUMBER.equals(header.getFirmwareImageMagicNumberHex())) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected FirmwareImageMagicNumber in header FW file: {}. Expected: {}.",
              header.getFirmwareImageMagicNumberHex(),
              FIRMWARE_IMAGE_MAGIC_NUMBER));
    }
    if (header.getHeaderLengthInt() != HEADER_LENGTH) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected length of header in header FW file: {}. Expected: {}.",
              header.getHeaderLengthInt(),
              HEADER_LENGTH));
    }
    if (header.getAddressLengthInt() != ADDRESS_LENGTH) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected length of address in header FW file: {}. Expected: {}.",
              header.getAddressLengthInt(),
              ADDRESS_LENGTH));
    }
    if (header.getAddressTypeInt() != ADDRESS_TYPE) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected type of address in header FW file: {}. Expected: {}.",
              header.getAddressTypeInt(),
              ADDRESS_TYPE));
    }
    if (header.getSecurityTypeInt() != SECURITY_TYPE) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected type of security in header FW file: {}. Expected: {}.",
              header.getSecurityTypeInt(),
              SECURITY_TYPE));
    }
    if (header.getSecurityLengthInt() != SECURITY_LENGTH) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected length of security in header FW file: {}. Expected: {}.",
              header.getSecurityLengthInt(),
              SECURITY_LENGTH));
    }
  }

  public byte[] createIV(final FirmwareImageData firmwareImageData) {
    final FirmwareImageDataHeaderAddressField firmwareImageDataHeaderAddressField =
        firmwareImageData.getHeader().getFirmwareImageDataHeaderAddressField();
    final byte[] iv =
        ByteBuffer.allocate(12)
            .put(firmwareImageDataHeaderAddressField.getMft())
            .put(firmwareImageDataHeaderAddressField.getId())
            .put(firmwareImageDataHeaderAddressField.getVersion())
            .put(firmwareImageDataHeaderAddressField.getType())
            .put(Arrays.reverse(firmwareImageData.getHeader().getFirmwareImageVersion()))
            .array();
    return iv;
  }
}
