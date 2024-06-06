// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class FirmwareFileDto implements Serializable {

  private static final long serialVersionUID = -4794626243032507358L;

  private final String firmwareIdentification;
  private final String deviceIdentification;
  private final byte[] firmwareFile;
  private final String hash;
  private final String hashType;

  public FirmwareFileDto(
      final String firmwareIdentification,
      final String deviceIdentification,
      final byte[] firmwareFile,
      final String hash,
      final String hashType) {
    this.firmwareIdentification = firmwareIdentification;
    this.deviceIdentification = deviceIdentification;
    this.firmwareFile = firmwareFile;
    this.hash = hash;
    this.hashType = hashType;
  }

  public String getFirmwareIdentification() {
    return this.firmwareIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public byte[] getFirmwareFile() {
    if (this.firmwareFile == null) {
      return new byte[] {};
    }
    return this.firmwareFile.clone();
  }

  public String getHash() {
    return this.hash;
  }

  public String getHashType() {
    return this.hashType;
  }
}
