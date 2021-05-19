/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.imagedata;

import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FirmwareImageData {

  private byte[] imageData;

  public FirmwareImageData(final byte[] imageData) {
    this.imageData = imageData;
    final Integer firmwareImageLength = this.getHeader().getFirmwareImageLengthInt();
    final Integer securityLength = this.getHeader().getSecurityLengthInt();
    final Integer headerLength = this.getHeader().getHeaderLengthInt();
    if (imageData.length != (firmwareImageLength + securityLength + headerLength)) {
      log.warn(
          "Byte array length doesn't match lengths defined in header: "
              + "\nByte array length : {}"
              + "\nLengths defined in header: "
              + "\nHeaderLength : {}"
              + "\nFirmwareImageLength : {}"
              + "\nSecurityLength : {}"
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
    final byte[] firmwareImageByteArray =
        this.readBytes(
            this.imageData,
            this.getHeader().getHeaderLengthInt(),
            this.getHeader().getHeaderLengthInt()
                + this.getHeader().getFirmwareImageLengthInt()
                - 1);
    return firmwareImageByteArray;
  }

  public void addIdentificationNumber(final Long mbusIdentificationNumber) {

    final byte[] mbusIdentificationNumberArray =
        intToByteArray(mbusIdentificationNumber.intValue());
    final ByteBuffer buffer = ByteBuffer.wrap(this.imageData);
    buffer.position(22);
    buffer.put(mbusIdentificationNumberArray);
    this.imageData = buffer.array();
  }

  public FirmwareImageDataHeader getHeader() {
    final FirmwareImageDataHeader firmwareImageDataHeader = new FirmwareImageDataHeader();

    firmwareImageDataHeader.setFirmwareImageMagicNumber(this.readBytes(this.imageData, 0, 3));
    firmwareImageDataHeader.setHeaderVersion(this.readBytes(this.imageData, 4, 4));
    firmwareImageDataHeader.setHeaderLength(this.readBytes(this.imageData, 5, 6));
    firmwareImageDataHeader.setFirmwareImageVersion(this.readBytes(this.imageData, 7, 10));
    firmwareImageDataHeader.setFirmwareImageLength(this.readBytes(this.imageData, 11, 14));
    firmwareImageDataHeader.setSecurityLength(this.readBytes(this.imageData, 15, 15));
    firmwareImageDataHeader.setSecurityType(this.readBytes(this.imageData, 17, 17));
    firmwareImageDataHeader.setAddressLength(this.readBytes(this.imageData, 18, 18));
    firmwareImageDataHeader.setAddressType(this.readBytes(this.imageData, 19, 19));
    this.setAddressField(firmwareImageDataHeader);
    firmwareImageDataHeader.setActivationType(this.readBytes(this.imageData, 28, 28));
    firmwareImageDataHeader.setActivationTime(this.readBytes(this.imageData, 29, 34));
    return firmwareImageDataHeader;
  }

  private static byte[] intToByteArray(final int i) {
    return new byte[] {
      (byte) ((i >> 24) & 0xFF),
      (byte) ((i >> 16) & 0xFF),
      (byte) ((i >> 8) & 0xFF),
      (byte) (i & 0xFF)
    };
  }

  private void setAddressField(final FirmwareImageDataHeader firmwareImageDataHeader) {
    final FirmwareImageDataHeaderAddressField firmwareImageDataHeaderAddressField =
        new FirmwareImageDataHeaderAddressField(
            this.readBytes(this.imageData, 20, 21),
            this.readBytes(this.imageData, 22, 25),
            this.readBytes(this.imageData, 26, 26),
            this.readBytes(this.imageData, 27, 27));
    firmwareImageDataHeader.setFirmwareImageDataHeaderAddressField(
        firmwareImageDataHeaderAddressField);
  }

  private byte[] readBytes(final byte[] bytes, final int begin, final int end) {
    return ByteBuffer.allocate(end - begin + 1).put(bytes, begin, (end - begin + 1)).array();
  }
}
