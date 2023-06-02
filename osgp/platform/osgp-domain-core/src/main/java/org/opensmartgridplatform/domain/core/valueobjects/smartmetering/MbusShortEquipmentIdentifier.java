//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class MbusShortEquipmentIdentifier implements Serializable {

  private static final long serialVersionUID = -8675174075140715801L;

  private final String identificationNumber;
  private final String manufacturerIdentification;
  private final Short versionIdentification;
  private final Short deviceTypeIdentification;

  public MbusShortEquipmentIdentifier(
      final String identificationNumber,
      final String manufacturerIdentification,
      final Short versionIdentification,
      final Short deviceTypeIdentification) {

    this.identificationNumber = identificationNumber;
    this.manufacturerIdentification = manufacturerIdentification;
    this.versionIdentification = versionIdentification;
    this.deviceTypeIdentification = deviceTypeIdentification;
  }

  @Override
  public String toString() {
    return String.format(
        "MbusShortEquipmentIdentifier[identificationNumber=%s, manufacturer=%s, version=%d, deviceType=%d]",
        this.identificationNumber,
        this.manufacturerIdentification,
        this.versionIdentification,
        this.deviceTypeIdentification);
  }

  public String getIdentificationNumber() {
    return this.identificationNumber;
  }

  public String getManufacturerIdentification() {
    return this.manufacturerIdentification;
  }

  public Short getVersionIdentification() {
    return this.versionIdentification;
  }

  public Short getDeviceTypeIdentification() {
    return this.deviceTypeIdentification;
  }
}
