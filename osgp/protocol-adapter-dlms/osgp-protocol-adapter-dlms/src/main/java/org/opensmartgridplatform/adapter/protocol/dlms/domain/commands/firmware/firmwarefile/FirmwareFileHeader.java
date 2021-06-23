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
import lombok.Setter;
import org.bouncycastle.util.encoders.Hex;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.ActivationType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.AddressType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.DeviceType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums.SecurityType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.ManufacturerId;

@Setter
public class FirmwareFileHeader {

  private byte[] firmwareImageMagicNumber;
  private byte[] headerVersion;
  private byte[] headerLength;
  private byte[] firmwareImageVersion;
  private byte[] firmwareImageLength;
  private byte[] securityLength;
  private byte[] securityType;
  private byte[] addressType;
  private byte[] addressLength;
  private FirmwareFileHeaderAddressField firmwareFileHeaderAddressField;
  private byte[] activationType;
  private byte[] activationTime;

  public DeviceType getMbusDeviceType() {
    return DeviceType.getByCode(this.getFirmwareFileHeaderAddressField().getMbusDeviceType()[0]);
  }

  int getMbusVersionInt() {
    return this.toInt(this.getFirmwareFileHeaderAddressField().getMbusVersion());
  }

  ManufacturerId getMbusManufacturerId() {
    return ManufacturerId.fromId(
        this.toInt(this.getFirmwareFileHeaderAddressField().getMbusManufacturerId()));
  }

  String getActivationTimeHex() {
    return Hex.toHexString(this.activationTime);
  }

  public ActivationType getActivationTypeEnum() {
    return ActivationType.getByCode(this.activationType[0]);
  }

  public int getAddressLengthInt() {
    return this.toInt(this.addressLength);
  }

  public AddressType getAddressTypeEnum() {
    return AddressType.getByCode(this.addressType[0]);
  }

  public SecurityType getSecurityTypeEnum() {
    return SecurityType.getByCode(this.securityType[0]);
  }

  public int getSecurityLengthInt() {
    return this.toInt(this.securityLength);
  }

  int getFirmwareImageLengthInt() {
    return this.toInt(this.firmwareImageLength);
  }

  public byte[] getFirmwareImageVersion() {
    return this.firmwareImageVersion;
  }

  public String getFirmwareImageVersionHex() {
    return Hex.toHexString(this.firmwareImageVersion);
  }

  public int getHeaderLengthInt() {
    return this.toInt(this.headerLength);
  }

  public int getHeaderVersionInt() {
    return this.toInt(this.headerVersion);
  }

  public String getFirmwareImageMagicNumberHex() {
    return Hex.toHexString(this.firmwareImageMagicNumber);
  }

  String getMbusDeviceIdentificationNumber() {
    return Hex.toHexString(
        this.getFirmwareFileHeaderAddressField().getMbusDeviceIdentificationNumber());
  }

  public FirmwareFileHeaderAddressField getFirmwareFileHeaderAddressField() {
    return this.firmwareFileHeaderAddressField;
  }

  @Override
  public String toString() {
    final StringBuilder result = new StringBuilder();
    result.append("FirmwareFileHeader");
    result.append(
        String.format(
            "%n  firmwareImageMagicNumber: %s (hex)", this.getFirmwareImageMagicNumberHex()));
    result.append(String.format("%n  headerVersion: %d (int)", this.getHeaderVersionInt()));
    result.append(String.format("%n  headerLength : %d (int)", this.getHeaderLengthInt()));
    result.append(
        String.format("%n  firmwareImageVersion : %s (hex)", this.getFirmwareImageVersionHex()));
    result.append(
        String.format("%n  firmwareImageLength : %d (int)", this.getFirmwareImageLengthInt()));
    result.append(String.format("%n  securityLength : %d (int)", this.getSecurityLengthInt()));
    result.append(String.format("%n  securityType: %s", this.getSecurityTypeEnum()));
    result.append(String.format("%n  addressType: %s", this.getAddressTypeEnum()));
    result.append(String.format("%n  addressLength: %d (int)", this.getAddressLengthInt()));
    result.append(String.format("%n  FirmwareHeaderAddressField"));
    result.append(
        String.format(
            "%n    manufacturerId: %s", this.getMbusManufacturerId().getIdentification()));
    result.append(
        String.format(
            "%n    mbusDeviceIdentificationNumber: %s (hex)",
            this.getMbusDeviceIdentificationNumber()));
    result.append(String.format("%n    version: %d (int)", this.getMbusVersionInt()));
    result.append(String.format("%n    deviceType: %s", this.getMbusDeviceType()));
    result.append(String.format("%n  activationType: %s", this.getActivationTypeEnum()));
    result.append(String.format("%n  activationTime: %s (hex)", this.getActivationTimeHex()));
    return result.toString();
  }

  private int toInt(final byte[] bytesLSBfirst) {
    final ByteBuffer intBuffer =
        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).put(bytesLSBfirst);
    intBuffer.rewind();
    return intBuffer.getInt();
  }
}
