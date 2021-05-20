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
import lombok.extern.slf4j.Slf4j;
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
 */
@Slf4j
public class FirmwareFile {

  private byte[] imageData;

  public static final String FIRMWARE_IMAGE_MAGIC_NUMBER = "534d5235";
  public static final int HEADER_VERSION = 0;

  public FirmwareFile(final byte[] imageData) {
    this.imageData = imageData;
    final Integer firmwareImageLength = this.getHeader().getFirmwareImageLengthInt();
    final Integer securityLength = this.getHeader().getSecurityLengthInt();
    final Integer headerLength = this.getHeader().getHeaderLengthInt();
    if (imageData.length != (firmwareImageLength + securityLength + headerLength)) {
      log.warn(
          "Byte array length doesn't match lengths defined in header: "
              + "\nByte array length : {}"
              + "\nLengths defined in header: "
              + "\nHeader : {}"
              + "\nFirmwareImage : {}"
              + "\nSecurity : {}"
              + "\nTotal of {}  bytes.",
          imageData.length,
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
    return this.readBytes(this.imageData, 0, this.getHeader().getHeaderLengthInt() - 1);
  }

  public byte[] getFirmwareImageByteArray() {
    return this.readBytes(
        this.imageData,
        this.getHeader().getHeaderLengthInt(),
        this.getHeader().getHeaderLengthInt() + this.getHeader().getFirmwareImageLengthInt() - 1);
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

  public void setMbusDeviceSerialNumber(final int mbusDeviceSerialNumber) {

    final byte[] mbusDeviceSerialNumberByteArray =
        ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(mbusDeviceSerialNumber).array();
    final ByteBuffer buffer = ByteBuffer.wrap(this.imageData);
    buffer.position(22);
    buffer.put(mbusDeviceSerialNumberByteArray);
    this.imageData = buffer.array();
  }

  public FirmwareFileHeader getHeader() {
    final FirmwareFileHeader firmwareFileHeader = new FirmwareFileHeader();

    firmwareFileHeader.setFirmwareImageMagicNumber(this.readBytes(this.imageData, 0, 3));
    firmwareFileHeader.setHeaderVersion(this.readBytes(this.imageData, 4, 4));
    firmwareFileHeader.setHeaderLength(this.readBytes(this.imageData, 5, 6));
    firmwareFileHeader.setFirmwareImageVersion(this.readBytes(this.imageData, 7, 10));
    firmwareFileHeader.setFirmwareImageLength(this.readBytes(this.imageData, 11, 14));
    firmwareFileHeader.setSecurityLength(this.readBytes(this.imageData, 15, 15));
    firmwareFileHeader.setSecurityType(this.readBytes(this.imageData, 17, 17));
    firmwareFileHeader.setAddressLength(this.readBytes(this.imageData, 18, 18));
    firmwareFileHeader.setAddressType(this.readBytes(this.imageData, 19, 19));
    this.setAddressField(firmwareFileHeader);
    firmwareFileHeader.setActivationType(this.readBytes(this.imageData, 28, 28));
    firmwareFileHeader.setActivationTime(this.readBytes(this.imageData, 29, 34));
    return firmwareFileHeader;
  }

  private void setAddressField(final FirmwareFileHeader firmwareFileHeader) {
    final FirmwareFileHeaderAddressField firmwareFileHeaderAddressField =
        new FirmwareFileHeaderAddressField(
            this.readBytes(this.imageData, 20, 21),
            this.readBytes(this.imageData, 22, 25),
            this.readBytes(this.imageData, 26, 26),
            this.readBytes(this.imageData, 27, 27));
    firmwareFileHeader.setFirmwareFileHeaderAddressField(firmwareFileHeaderAddressField);
  }

  private byte[] readBytes(final byte[] bytes, final int begin, final int end) {
    return ByteBuffer.allocate(end - begin + 1).put(bytes, begin, (end - begin + 1)).array();
  }
}
