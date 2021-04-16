/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class MbusShortEquipmentIdentifierDto implements Serializable {

  private static final long serialVersionUID = 6292753531006326645L;

  private final String identificationNumber;
  private final String manufacturerIdentification;
  private final Short versionIdentification;
  private final Short deviceTypeIdentification;

  public MbusShortEquipmentIdentifierDto(
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
        "MbusShortEquipmentIdentifierDto[identificationNumber=%s, manufacturer=%s, version=%d, deviceType=%d]",
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
