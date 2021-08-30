/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class UpdateFirmwareRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 4779593744529504287L;

  private final String firmwareIdentification;
  private final String deviceIdentification;

  public UpdateFirmwareRequestDto(
      final String firmwareIdentification, final String deviceIdentification) {
    this.firmwareIdentification = firmwareIdentification;
    this.deviceIdentification = deviceIdentification;
  }

  public String getFirmwareIdentification() {
    return this.firmwareIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }
}
