// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
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
  private final byte[] imageIdentifier;

  public FirmwareFileDto(
      final String firmwareIdentification,
      final String deviceIdentification,
      final byte[] firmwareFile,
      final byte[] imageIdentifier) {
    this.firmwareIdentification = firmwareIdentification;
    this.deviceIdentification = deviceIdentification;
    this.firmwareFile = firmwareFile;
    this.imageIdentifier = imageIdentifier;
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

  public byte[] getImageIdentifier() {
    return this.imageIdentifier;
  }
}
