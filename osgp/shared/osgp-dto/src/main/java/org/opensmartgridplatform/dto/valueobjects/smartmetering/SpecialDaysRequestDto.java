/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SpecialDaysRequestDto implements Serializable {

  private static final long serialVersionUID = 5377631203726277887L;

  private final String deviceIdentification;

  private final SpecialDaysRequestDataDto specialDaysRequestData;

  public SpecialDaysRequestDto(
      final String deviceIdentification, final SpecialDaysRequestDataDto specialDaysRequestData) {
    super();
    this.deviceIdentification = deviceIdentification;
    this.specialDaysRequestData = specialDaysRequestData;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public SpecialDaysRequestDataDto getSpecialDaysRequestData() {
    return this.specialDaysRequestData;
  }
}
