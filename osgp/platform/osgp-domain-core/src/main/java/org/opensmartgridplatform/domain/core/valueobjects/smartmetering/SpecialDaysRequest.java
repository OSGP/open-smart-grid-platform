/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class SpecialDaysRequest implements Serializable {

  private static final long serialVersionUID = 2863312762786033679L;

  private final String deviceIdentification;

  private final SpecialDaysRequestData specialDaysRequestData;

  public SpecialDaysRequest(
      final String deviceIdentification, final SpecialDaysRequestData specialDaysRequestData) {
    super();
    this.deviceIdentification = deviceIdentification;
    this.specialDaysRequestData = specialDaysRequestData;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public SpecialDaysRequestData getSpecialDaysRequestData() {
    return this.specialDaysRequestData;
  }
}
