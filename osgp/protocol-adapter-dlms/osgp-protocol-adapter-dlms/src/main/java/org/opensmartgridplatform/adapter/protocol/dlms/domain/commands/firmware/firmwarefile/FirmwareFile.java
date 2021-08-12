/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

/**
 * This Firmware file is defined according to the Smart Meter Requirements 5.1, Supplement 4, P2
 * Companion Standard, Section 7. Firmware Upgrade
 *
 * <p>Fixed values are
 *
 * <ul>
 *   <li>Firmware image magic number: 53h 4Dh 52h 35h
 *   <li>Header Version: 00h
 * </ul>
 */
@Slf4j
public class FirmwareFile {

  private byte[] imageData;

  public static final String FIRMWARE_IMAGE_MAGIC_NUMBER = "534d5235";
  public static final int HEADER_VERSION = 0;

  public FirmwareFile(final byte[] imageData) {
    this.imageData = imageData;
  }

  public boolean isMbusFirmware() {
    return this.imageData.length >= 35
        && this.getHeader()
            .getFirmwareImageMagicNumberHex()
            .equalsIgnoreCase(FIRMWARE_IMAGE_MAGIC_NUMBER)
        && this.getHeader().getHeaderVersionInt() == HEADER_VERSION;
  }

  public void checkLengths() {
    final FirmwareFileHeader header = this.getHeader();
    final Integer firmwareImageLength = header.getFirmwareImageLengthInt();
    final Integer securityLength = header.getSecurityLengthInt();
    final Integer headerLength = header.getHeaderLengthInt();
    if (this.imageData.length != (firmwareImageLength + securityLength + headerLength)) {
      log.warn(
          "Byte array length doesn't match lengths defined in header: "
              + "\nByte array length : {}"
              + "\nLengths defined in header: "
              + "\nHeader : {}"
              + "\nFirmwareImage : {}"
              + "\nSecurity : {}"
              + "\nTotal of {}  bytes.",
          this.imageData.length,
          headerLength,
          firmwareImageLength,
          securityLength,
          (firmwareImageLength + securityLength + headerLength));
    }
  }

  public int length() {
    return this.imageData.length;
  }

  public byte[] getByteArray() {
    return this.imageData;
  }

  public byte[] getHeaderByteArray() {
    return this.readBytes(this.imageData, 0, this.getHeader().getHeaderLengthInt());
  }

  public byte[] getFirmwareImageByteArray() {
    return this.readBytes(
        this.imageData,
        this.getHeader().getHeaderLengthInt(),
        this.getHeader().getHeaderLengthInt() + this.getHeader().getFirmwareImageLengthInt());
  }

  public void setSecurityByteArray(final byte[] security) throws ProtocolAdapterException {
    if (security.length != this.getHeader().getSecurityLengthInt()) {
      throw new ProtocolAdapterException(
          "Length of generated security byte array differs from length defined in header of firmwarefile");
    }
    this.imageData =
        ByteBuffer.allocate(this.length())
            .put(this.getHeaderByteArray())
            .put(this.getFirmwareImageByteArray())
            .put(security)
            .array();
  }

  public void setMbusDeviceIdentificationNumber(final int mbusDeviceIdentificationNumber)
      throws ProtocolAdapterException {

    final byte[] mbusDeviceIdentificationNumberByteArray =
        ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(mbusDeviceIdentificationNumber)
            .array();

    this.checkWildcard(
        Hex.toHexString(
            ByteBuffer.allocate(4)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(mbusDeviceIdentificationNumber)
                .array()));

    final ByteBuffer buffer = ByteBuffer.wrap(this.imageData);
    buffer.position(22);
    buffer.put(mbusDeviceIdentificationNumberByteArray);
    this.imageData = buffer.array();
  }

  /**
   * The Identification number can be wildcarded, a firmware file can be made available for a range
   * or all individual meters. The wildcard character is hex-value: 'F'.
   */
  private void checkWildcard(final String mbusDeviceIdentificationNumberHex)
      throws ProtocolAdapterException {
    final String lsbFirstPattern = this.getHeader().getMbusDeviceIdentificationNumber();
    final String msbFirstPattern = this.reverseHexString(lsbFirstPattern);
    if (!Pattern.matches(
        msbFirstPattern.replaceAll("[fF]", "[0-9]"), mbusDeviceIdentificationNumberHex)) {
      throw new ProtocolAdapterException(
          String.format(
              "M-Bus Device Identification Number (%s) does not fit the range of Identification Numbers supported by this Firmware File (%s)",
              mbusDeviceIdentificationNumberHex, msbFirstPattern));
    }
  }

  private String reverseHexString(final String hex) {
    String hexIn = hex;
    if (hexIn.length() % 2 != 0) {
      hexIn = "0" + hexIn;
    }
    final int numberOfPairs = hexIn.length() / 2;
    final StringBuilder patternBuilder = new StringBuilder(hexIn.length());
    for (int i = 0; i < numberOfPairs; i++) {
      final int positionAfterPairToCopy = 2 * (numberOfPairs - i);
      patternBuilder
          .append(hexIn.charAt(positionAfterPairToCopy - 2))
          .append(hexIn.charAt(positionAfterPairToCopy - 1));
    }
    return patternBuilder.toString();
  }

  public FirmwareFileHeader getHeader() {
    final FirmwareFileHeader firmwareFileHeader = new FirmwareFileHeader();

    firmwareFileHeader.setFirmwareImageMagicNumber(this.readBytes(this.imageData, 0, 4));
    firmwareFileHeader.setHeaderVersion(this.readBytes(this.imageData, 4, 5));
    firmwareFileHeader.setHeaderLength(this.readBytes(this.imageData, 5, 7));
    firmwareFileHeader.setFirmwareImageVersion(this.readBytes(this.imageData, 7, 11));
    firmwareFileHeader.setFirmwareImageLength(this.readBytes(this.imageData, 11, 15));
    firmwareFileHeader.setSecurityLength(this.readBytes(this.imageData, 15, 16));
    firmwareFileHeader.setSecurityType(this.readBytes(this.imageData, 17, 18));
    firmwareFileHeader.setAddressLength(this.readBytes(this.imageData, 18, 19));
    firmwareFileHeader.setAddressType(this.readBytes(this.imageData, 19, 20));
    this.setAddressField(firmwareFileHeader);
    firmwareFileHeader.setActivationType(this.readBytes(this.imageData, 28, 29));
    firmwareFileHeader.setActivationTime(this.readBytes(this.imageData, 29, 35));
    return firmwareFileHeader;
  }

  private void setAddressField(final FirmwareFileHeader firmwareFileHeader) {
    final FirmwareFileHeaderAddressField firmwareFileHeaderAddressField =
        new FirmwareFileHeaderAddressField(
            this.readBytes(this.imageData, 20, 22),
            this.readBytes(this.imageData, 22, 26),
            this.readBytes(this.imageData, 26, 27),
            this.readBytes(this.imageData, 27, 28));
    firmwareFileHeader.setFirmwareFileHeaderAddressField(firmwareFileHeaderAddressField);
  }

  public String getHeaderHex() {
    return Hex.toHexString(this.readBytes(this.imageData, 0, 35));
  }

  private byte[] readBytes(final byte[] bytes, final int begin, final int end) {
    return Arrays.copyOfRange(bytes, begin, end);
  }

  public byte[] createImageIdentifierForMbusDevice() {
    /*
     * The Identifier for firmware image of M-Bus device has the following content
     * - MAN (3 bytes) Manufacturer code according to FLAG (https://www.dlms.com/flag-id/flag-id-list)
     * - DEV (4 bytes) M-Bus DEV code (letters "MBUS")
     * - M-Bus Short ID (8 bytes)
     *   - (3 bytes) Identification Number
     *   - (3 bytes) Manufacturer ID
     *   - (1 bytes) Version
     *   - (1 byte ) DeviceType
     * - M-Bus FW ID (4 bytes)
     */
    final FirmwareFileHeader header = this.getHeader();
    final FirmwareFileHeaderAddressField addressField = header.getFirmwareFileHeaderAddressField();
    final int imageIdentifierSize = 19;
    if (log.isDebugEnabled()) {
      log.debug("creating image identifier for M-Bus device from firmware file header information");
      log.debug("MbusManufacturerId " + Arrays.toString(addressField.getMbusManufacturerId()));
      log.debug("MBUS " + Arrays.toString("MBUS".getBytes()));
      log.debug(
          "MbusDeviceIdentificationNumber "
              + Arrays.toString(addressField.getMbusDeviceIdentificationNumber()));
      log.debug("MbusManufacturerId " + Arrays.toString(addressField.getMbusManufacturerId()));
      log.debug("MbusVersion " + Arrays.toString(addressField.getMbusVersion()));
      log.debug("MbusDeviceType " + Arrays.toString(addressField.getMbusDeviceType()));
      log.debug("FirmwareImageVersion " + Arrays.toString(header.getFirmwareImageVersion()));
      log.debug("Total size of image identifier : " + imageIdentifierSize + " bytes");
    }

    final ByteBuffer imageIdentifier = ByteBuffer.allocate(imageIdentifierSize);
    imageIdentifier.put(addressField.getMbusManufacturerId());
    imageIdentifier.put("MBUS".getBytes());
    imageIdentifier.put(addressField.getMbusDeviceIdentificationNumber());
    imageIdentifier.put(addressField.getMbusManufacturerId());
    imageIdentifier.put(addressField.getMbusVersion());
    imageIdentifier.put(addressField.getMbusDeviceType());
    imageIdentifier.put(header.getFirmwareImageVersion());

    return imageIdentifier.array();
  }
}
