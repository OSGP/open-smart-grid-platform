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
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

@Getter
@Setter
public class FirmwareImageDataHeader {

  private byte[] firmwareImageMagicNumber;
  private byte[] headerVersion;
  private byte[] headerLength;
  private byte[] firmwareImageVersion;
  private byte[] firmwareImageLength;
  private byte[] securityLength;
  private byte[] securityType;
  private byte[] addressType;
  private byte[] addressLength;
  private FirmwareImageDataHeaderAddressField firmwareImageDataHeaderAddressField;
  private byte[] activationType;
  private byte[] activationTime;

  Integer getTypeInt() {
    return this.toInteger(this.getFirmwareImageDataHeaderAddressField().getType());
  }

  Integer getVersionInt() {
    return this.toInteger(this.getFirmwareImageDataHeaderAddressField().getVersion());
  }

  String getMftHex() {
    return Hex.toHexString(this.getFirmwareImageDataHeaderAddressField().getMft());
  }

  String getActivationTimeHex() {
    return Hex.toHexString(this.activationTime);
  }

  Integer getActivationTypeInt() {
    return this.toInteger(this.activationType);
  }

  public Integer getAddressLengthInt() {
    return this.toInteger(Arrays.reverse(this.addressLength));
  }

  public Integer getAddressTypeInt() {
    return this.toInteger(this.addressType);
  }

  public Integer getSecurityTypeInt() {
    return this.toInteger(this.securityType);
  }

  public Integer getSecurityLengthInt() {
    return this.toInteger(Arrays.reverse(this.securityLength));
  }

  String getFirmwareImageLengthHex() {
    return Hex.toHexString(this.firmwareImageLength);
  }

  Integer getFirmwareImageLengthInt() {
    return this.toInteger2(Arrays.reverse(this.firmwareImageLength));
  }

  String getFirmwareImageVersionHex() {
    return Hex.toHexString(this.firmwareImageVersion);
  }

  String getHeaderLengthHex() {
    return Hex.toHexString(this.headerLength);
  }

  public Integer getHeaderLengthInt() {
    return this.toInteger(Arrays.reverse(this.headerLength));
  }

  Integer getHeaderVersionInt() {
    return this.toInteger(this.headerVersion);
  }

  public String getFirmwareImageMagicNumberHex() {
    return Hex.toHexString(this.firmwareImageMagicNumber);
  }

  String getIdentificationNumber() {
    return Hex.toHexString(this.getFirmwareImageDataHeaderAddressField().getId());
  }

  @Override
  public String toString() {
    final StringBuilder result = new StringBuilder();
    result.append("FirmwareImageDataHeader");
    result.append(
        String.format(
            "%n  firmwareImageMagicNumber: %s (hex)", this.getFirmwareImageMagicNumberHex()));
    result.append(String.format("%n  headerVersion: %d (int)", this.getHeaderVersionInt()));
    result.append(
        String.format(
            "%n  headerLength : %s (hex) --> %d (int)",
            this.getHeaderLengthHex(), this.getHeaderLengthInt()));
    result.append(
        String.format("%n  firmwareImageVersion : %s (hex)", this.getFirmwareImageVersionHex()));
    result.append(
        String.format(
            "%n  firmwareImageLength : %s (hex) --> %d (int)",
            this.getFirmwareImageLengthHex(), this.getFirmwareImageLengthInt()));
    result.append(String.format("%n  securityLength : %d (int)", this.getSecurityLengthInt()));
    result.append(String.format("%n  securityType: %d (int)", this.getSecurityTypeInt()));
    result.append(String.format("%n  addressType: %d (int)", this.getAddressTypeInt()));
    result.append(String.format("%n  addressLength: %d (int)", this.getAddressLengthInt()));
    result.append(String.format("%n  FirmwareHeaderAddressField"));
    result.append(String.format("%n    mft: %s (hex)", this.getMftHex()));
    result.append(String.format("%n    id: %s (hex)", this.getIdentificationNumber()));
    result.append(String.format("%n    version: %d (int)", this.getVersionInt()));
    result.append(String.format("%n    type: %d (int)", this.getTypeInt()));
    result.append(String.format("%n  activationType: %d (int)", this.getActivationTypeInt()));
    result.append(String.format("%n  activationTime: %s (hex)", this.getActivationTimeHex()));
    return result.toString();
  }

  private Integer toInteger(final byte[] bytes) {
    return Integer.parseInt(Hex.toHexString(bytes), 16);
  }

  private Integer toInteger2(final byte[] bytes) {
    return ByteBuffer.wrap(bytes).getInt();
  }
}
