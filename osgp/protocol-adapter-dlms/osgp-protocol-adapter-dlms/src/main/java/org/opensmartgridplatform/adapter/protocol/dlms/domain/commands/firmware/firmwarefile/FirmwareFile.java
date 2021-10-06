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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.AddressType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

/**
 * This Firmware file is defined according to the Smart Meter Requirements 5.1, Supplement 4, P2
 * Companion Standard, Section 7. Firmware Upgrade
 *
 * <p>The header has a fixed length of 35 positions
 *
 * <p>Fixed values are
 *
 * <ul>
 *   <li>Firmware image magic number: 53h 4Dh 52h 35h
 *   <li>Header Version: 00h
 *   <li>Address Type: 01h (for M-Bus device firmware)
 * </ul>
 */
@Slf4j
public class FirmwareFile {

  private static final byte[] EMPTY_FIRMWARE_VERSION = new byte[] {0, 0, 0, 0};

  private byte[] imageData;

  private static final int HEADER_LENGTH = 35;
  //  public static final String FIRMWARE_IMAGE_MAGIC_NUMBER = "534d5235";
  public static final List<String> VALID_FIRMWARE_IMAGE_MAGIC_NUMBERS =
      Arrays.asList("534d5235", "35524d53");
  // Fixed value in requirement of SMR5.1. In SMR5.2 no value is specified for HEADER_VERSION
  // Therefor there is no check on the value of HEADER_VERSION
  public static final int HEADER_VERSION = 0;

  public FirmwareFile(final byte[] imageData) {
    this.imageData = imageData;
  }

  public boolean isMbusFirmware() {
    return this.imageData.length >= HEADER_LENGTH
        && VALID_FIRMWARE_IMAGE_MAGIC_NUMBERS.contains(
            this.getHeader().getFirmwareImageMagicNumberHex())
        && this.getHeader().getAddressTypeEnum() == AddressType.MBUS_ADDRESS;
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

  public void setMbusDeviceIdentificationNumber(final String mbusDeviceIdentificationNumber)
      throws ProtocolAdapterException {

    final byte[] mbusDeviceIdentificationNumberByteArray =
        ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(Integer.parseInt(mbusDeviceIdentificationNumber, 16))
            .array();

    this.checkWildcard(mbusDeviceIdentificationNumber);

    final ByteBuffer buffer = ByteBuffer.wrap(this.imageData);
    buffer.position(22);
    buffer.put(mbusDeviceIdentificationNumberByteArray);
    this.imageData = buffer.array();
  }

  public void setMbusVersion(final Integer mbusVersion) throws ProtocolAdapterException {

    final ByteBuffer buffer = ByteBuffer.wrap(this.imageData);
    buffer.position(26);
    buffer.put(new byte[] {mbusVersion.byteValue()});
    this.imageData = buffer.array();
  }

  /**
   * The Identification number can be wildcarded, a firmware file can be made available for a range
   * or all individual meters. The wildcard character is hex-value: 'F'.
   */
  private void checkWildcard(final String mbusDeviceIdentificationNumber)
      throws ProtocolAdapterException {
    final String lsbFirstPattern = this.getHeader().getMbusDeviceIdentificationNumber();
    final String msbFirstPattern = this.reverseHexString(lsbFirstPattern);
    if (!Pattern.matches(
        msbFirstPattern.replaceAll("[fF]", "[0-9]"), mbusDeviceIdentificationNumber)) {
      throw new ProtocolAdapterException(
          String.format(
              "M-Bus Device Identification Number (%s) does not fit the range of Identification Numbers supported by this Firmware File (%s)",
              mbusDeviceIdentificationNumber, msbFirstPattern));
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
    firmwareFileHeader.setActivationTime(this.readBytes(this.imageData, 29, HEADER_LENGTH));
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
    return Hex.toHexString(this.readBytes(this.imageData, 0, HEADER_LENGTH));
  }

  private byte[] readBytes(final byte[] bytes, final int begin, final int end) {
    return Arrays.copyOfRange(bytes, begin, end);
  }

  /**
   * Smart Meter Requirements 5.1, Supplement 5, P3 Companion Standard, Section 5.13.1 Firmware
   * upgrade M-Bus devices
   *
   * <p>The Identifier for firmware image of M-Bus device has the following content
   *
   * <ul>
   *   <li>MAN (3 bytes) Manufacturer code according to FLAG
   *       (https://www.dlms.com/flag-id/flag-id-list)
   *   <li>DEV (4 bytes) M-Bus DEV code (letters "MBUS" (utf-8))
   *   <li>M-Bus Short ID*
   *       <ul>
   *         <li>Identification Number (4 bytes)
   *         <li>Manufacturer ID (2 bytes)
   *         <li>Version (1 bytes)
   *         <li>DeviceType (1 bytes)
   *       </ul>
   *   <li>M-Bus FW ID (4 bytes)
   * </ul>
   *
   * * Definition of Short-ID is described in Smart Meter Requirements 5.2 Supplement 5, P2
   * Companion Standard paragraph 9.2. Short Equipment Identifier
   *
   * @return byte[] image identifier
   */
  public byte[] createImageIdentifierForMbusDevice() {

    final FirmwareFileHeader header = this.getHeader();
    final int imageIdentifierSize = 19;

    if (log.isDebugEnabled()) {
      this.logImageIdentifierDetails(header, imageIdentifierSize);
    }

    final FirmwareFileHeaderAddressField addressField = header.getFirmwareFileHeaderAddressField();
    final ByteBuffer imageIdentifier = ByteBuffer.allocate(imageIdentifierSize);
    imageIdentifier.put(
        header.getMbusManufacturerId().getIdentification().getBytes(StandardCharsets.UTF_8));
    imageIdentifier.put("MBUS".getBytes(StandardCharsets.UTF_8));
    imageIdentifier.put(addressField.getMbusDeviceIdentificationNumber());
    imageIdentifier.put(addressField.getMbusManufacturerId());
    imageIdentifier.put(addressField.getMbusVersion());
    imageIdentifier.put(addressField.getMbusDeviceType());
    // There is no validation on the Firmware ID part of the imageIdentifier
    // for the meters tested so far. As long as no meters are available that explicitly
    // validate this part empty bytes are placed here
    imageIdentifier.put(EMPTY_FIRMWARE_VERSION);

    return imageIdentifier.array();
  }

  private void logImageIdentifierDetails(
      final FirmwareFileHeader header, final int imageIdentifierSize) {
    final FirmwareFileHeaderAddressField addressField = header.getFirmwareFileHeaderAddressField();
    log.debug("creating image identifier for M-Bus device from firmware file header information");
    final String manufacturerIdentification = header.getMbusManufacturerId().getIdentification();
    log.debug(
        "ManufacturerIdentification ('"
            + manufacturerIdentification
            + "') "
            + Arrays.toString(manufacturerIdentification.getBytes(StandardCharsets.UTF_8)));
    log.debug("'MBUS' " + Arrays.toString("MBUS".getBytes(StandardCharsets.UTF_8)));
    log.debug(
        "MbusDeviceIdentificationNumber "
            + Arrays.toString(addressField.getMbusDeviceIdentificationNumber()));
    log.debug("MbusManufacturerId " + Arrays.toString(addressField.getMbusManufacturerId()));
    log.debug("MbusVersion " + Arrays.toString(addressField.getMbusVersion()));
    log.debug("MbusDeviceType " + Arrays.toString(addressField.getMbusDeviceType()));
    log.debug("FirmwareImageVersion " + Arrays.toString(header.getFirmwareImageVersion()));
    log.debug("Total size of image identifier : " + imageIdentifierSize + " bytes");
  }
}
