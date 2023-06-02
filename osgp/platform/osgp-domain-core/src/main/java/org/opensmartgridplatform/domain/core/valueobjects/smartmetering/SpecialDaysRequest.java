//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
