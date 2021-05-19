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
        "\n\tfirmwareImageMagicNumber: " + this.getFirmwareImageMagicNumberHex() + " (hex)");
    result.append("\n\theaderVersion: " + this.getHeaderVersionInt() + " (int)");
    result.append(
        "\n\theaderLength : "
            + this.getHeaderLengthHex()
            + " (hex) --> "
            + this.getHeaderLengthInt()
            + " (int)");
    result.append("\n\tfirmwareImageVersion : " + this.getFirmwareImageVersionHex() + " (hex)");
    result.append(
        "\n\tfirmwareImageLength : "
            + this.getFirmwareImageLengthHex()
            + " (hex) --> "
            + this.getFirmwareImageLengthInt()
            + " (int)");
    result.append("\n\tsecurityLength : " + this.getSecurityLengthInt() + " (int)");
    result.append("\n\tsecurityType: " + this.getSecurityTypeInt() + " (int)");
    result.append("\n\taddressType: " + this.getAddressTypeInt() + " (int)");
    result.append("\n\taddressLength: " + this.getAddressLengthInt() + " (int)");
    result.append("\n\tFirmwareHeaderAddressField");
    result.append("\n\t\tmft: " + this.getMftHex() + " (hex)");
    result.append("\n\t\tid: " + this.getIdentificationNumber() + " (hex)");
    result.append("\n\t\tversion: " + this.getVersionInt() + " (int)");
    result.append("\n\t\ttype: " + this.getTypeInt() + " (int)");
    result.append("\n\tactivationType: " + this.getActivationTypeInt() + " (int)");
    result.append("\n\tactivationTime: " + this.getActivationTimeHex() + " (hex)");
    return result.toString();
  }

  private Integer toInteger(final byte[] bytes) {
    return Integer.parseInt(Hex.toHexString(bytes), 16);
  }

  private Integer toInteger2(final byte[] bytes) {
    return ByteBuffer.wrap(bytes).getInt();
  }
}
