//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.FirmwareFile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.FirmwareFileHeader;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.FirmwareFileHeaderAddressField;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.ActivationType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.AddressType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.DeviceType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.SecurityType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service generates a MAC as security part of a Gas Meter Firmware file as described in Smart
 * Meter Requirements 5.1, Supplement 4, P2 Companion Standard, Section 7. Firmware Upgrade
 *
 * <p>As versions earlier than 5.0 of the Smart Meter Requirements do not support the upgrade of Gas
 * Meter Firmware this implementation only supports Gas Meter Firmware files with magic number
 * 534d5235 ('SMR5') As this is the first and only specification most entries in the Firmware File's
 * header are fixed
 *
 * <ul>
 *   <li>Firmware image magic number: 53h 4Dh 52h 35h
 *   <li>Header Version: 00h
 *   <li>Header length: 35
 *   <li>Security length: 16
 *   <li>Security type: GMAC
 *   <li>Address length: 8
 *   <li>Address type: M-Bus address
 *   <li>Device type: Gas
 *   <li>Activation type: Master initiated activation
 * </ul>
 */
@Service
@Slf4j
public class MacGenerationService {

  private static final int HEADER_LENGTH = 35;
  private static final int ADDRESS_LENGTH = 8;
  private static final int SECURITY_LENGTH = 16;

  @Autowired private SecretManagementService secretManagementService;

  public byte[] calculateMac(
      final MessageMetadata messageMetadata,
      final String deviceIdentification,
      final FirmwareFile firmwareFile)
      throws ProtocolAdapterException {

    final FirmwareFileHeader header = firmwareFile.getHeader();
    this.validateHeader(header);

    final byte[] iv = this.createIV(firmwareFile);

    log.debug("Calculated IV: {}", Hex.toHexString(iv));

    final byte[] decryptedFirmwareUpdateAuthenticationKey =
        this.secretManagementService.getKey(
            messageMetadata,
            deviceIdentification,
            SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION);

    if (decryptedFirmwareUpdateAuthenticationKey == null
        || decryptedFirmwareUpdateAuthenticationKey.length == 0) {
      throw new ProtocolAdapterException(
          String.format(
              "No key of type %s found for device %s",
              SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION, deviceIdentification));
    }

    final CipherParameters cipherParameters =
        new KeyParameter(decryptedFirmwareUpdateAuthenticationKey);
    final ParametersWithIV parameterWithIV = new ParametersWithIV(cipherParameters, iv);

    final int macSizeBits = header.getSecurityLengthInt() * 8;
    final GMac mac = new GMac(new GCMBlockCipher(new AESEngine()), macSizeBits);

    mac.init(parameterWithIV);

    final byte[] headerByteArray = firmwareFile.getHeaderByteArray();
    final byte[] firmwareImageByteArray = firmwareFile.getFirmwareImageByteArray();
    final byte[] input =
        ByteBuffer.allocate(headerByteArray.length + firmwareImageByteArray.length)
            .put(headerByteArray)
            .put(firmwareImageByteArray)
            .array();

    mac.update(input, 0, input.length);
    final byte[] generatedMac = new byte[mac.getMacSize()];
    mac.doFinal(generatedMac, 0);

    if (header.getSecurityLengthInt() != generatedMac.length) {
      throw new ProtocolAdapterException(
          String.format(
              "Unable to generate correct MAC: Defined security length in firmware header (%d) differs from length of generated MAC (%d)",
              header.getSecurityLengthInt(), generatedMac.length));
    }
    return generatedMac;
  }

  private void validateHeader(final FirmwareFileHeader header) throws ProtocolAdapterException {
    if (!FirmwareFile.VALID_FIRMWARE_IMAGE_MAGIC_NUMBERS.contains(
        header.getFirmwareImageMagicNumberHex())) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected FirmwareImageMagicNumber in header firmware file: %s. Expected one of %s.",
              header.getFirmwareImageMagicNumberHex(),
              FirmwareFile.VALID_FIRMWARE_IMAGE_MAGIC_NUMBERS));
    }
    if (header.getHeaderLengthInt() != HEADER_LENGTH) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected length of header in header firmware file: %d. Expected: %d.",
              header.getHeaderLengthInt(), HEADER_LENGTH));
    }
    if (header.getAddressLengthInt() != ADDRESS_LENGTH) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected length of address in header firmware file: %d. Expected: %d.",
              header.getAddressLengthInt(), ADDRESS_LENGTH));
    }
    if (header.getAddressTypeEnum() != AddressType.MBUS_ADDRESS) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected type of address in header firmware file: %s. Expected: %s.",
              header.getAddressTypeEnum(), AddressType.MBUS_ADDRESS.name()));
    }
    if (header.getSecurityTypeEnum() != SecurityType.GMAC) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected type of security in header firmware file: %s. Expected: %s.",
              header.getSecurityTypeEnum(), SecurityType.GMAC.name()));
    }
    if (header.getMbusDeviceType() != DeviceType.GAS) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected type of device in header firmware file: %s. Expected: %s.",
              header.getMbusDeviceType(), DeviceType.GAS.name()));
    }
    if (header.getSecurityLengthInt() != SECURITY_LENGTH) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected length of security in header firmware file: %d. Expected: %d.",
              header.getSecurityLengthInt(), SECURITY_LENGTH));
    }
    if (header.getActivationTypeEnum() != ActivationType.MASTER_INITIATED_ACTIVATION) {
      throw new ProtocolAdapterException(
          String.format(
              "Unexpected type of activation in header firmware file: %s. Expected: %s.",
              header.getActivationTypeEnum(), ActivationType.MASTER_INITIATED_ACTIVATION));
    }
  }

  public byte[] createIV(final FirmwareFile firmwareFile) {
    final FirmwareFileHeaderAddressField firmwareFileHeaderAddressField =
        firmwareFile.getHeader().getFirmwareFileHeaderAddressField();
    final int ivLength = 12;
    this.logIV(firmwareFile, ivLength);
    return ByteBuffer.allocate(ivLength)
        .put(firmwareFileHeaderAddressField.getMbusManufacturerId())
        .put(firmwareFileHeaderAddressField.getMbusDeviceIdentificationNumber())
        .put(firmwareFileHeaderAddressField.getMbusVersion())
        .put(firmwareFileHeaderAddressField.getMbusDeviceType())
        .put(Arrays.reverse(firmwareFile.getHeader().getFirmwareImageVersion()))
        .array();
  }

  private void logIV(final FirmwareFile firmwareFile, final int ivLength) {
    final FirmwareFileHeaderAddressField firmwareFileHeaderAddressField =
        firmwareFile.getHeader().getFirmwareFileHeaderAddressField();
    log.debug("IV length: {}", ivLength);
    final Map<String, byte[]> map = new HashMap<>(5);
    map.put("MbusManufacturerId", firmwareFileHeaderAddressField.getMbusManufacturerId());
    map.put(
        "MbusDeviceIdentificationNumber",
        firmwareFileHeaderAddressField.getMbusDeviceIdentificationNumber());
    map.put("MbusVersion", firmwareFileHeaderAddressField.getMbusVersion());
    map.put("MbusDeviceType", firmwareFileHeaderAddressField.getMbusDeviceType());
    map.put("FirmwareImageVersion", firmwareFile.getHeader().getFirmwareImageVersion());

    for (final Entry<String, byte[]> entry : map.entrySet()) {
      log.debug(
          entry.getKey() + ": (Hex){} ({})",
          Hex.toHexString(entry.getValue()),
          entry.getValue().length);
    }
  }
}
