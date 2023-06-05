// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
