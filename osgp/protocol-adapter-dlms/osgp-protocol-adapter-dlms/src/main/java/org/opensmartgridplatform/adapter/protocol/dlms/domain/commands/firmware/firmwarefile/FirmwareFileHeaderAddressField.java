/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile;

import lombok.Getter;

/**
 * M-Bus address is according to EN13757-4 standard which is a concatenation of: 'Manufacturer
 * identification' [MAN or M-field], ‘Identification number’ [ID], ‘Version number’ [VER] and
 * ‘Device type information’ [DEV]. The Manufacturer identification can never be wildcarded, a
 * firmware file is manufacturer specific. The Device type can never be wildcarded, a firmware file
 * is always for a certain type of M-Bus device. The Version number can be wildcarded, a firmware
 * file can be made available for several hardware revisions. The Identification number can be
 * wildcarded, a firmware file can be made available for a range or all individual meters. The
 * wildcard character is hex-value: 'F'.
 */
@Getter
public class FirmwareFileHeaderAddressField {

  private byte[] mbusManufacturerId;
  private byte[] mbusDeviceIdentificationNumber;
  private byte[] mbusVersion;
  private byte[] mbusDeviceType;

  public FirmwareFileHeaderAddressField(
      final byte[] mbusManufacturerId,
      final byte[] mbusDeviceIdentificationNumber,
      final byte[] mbusVersion,
      final byte[] mbusDeviceType) {
    this.mbusManufacturerId = mbusManufacturerId;
    this.mbusDeviceIdentificationNumber = mbusDeviceIdentificationNumber;
    this.mbusVersion = mbusVersion;
    this.mbusDeviceType = mbusDeviceType;
  }
}
