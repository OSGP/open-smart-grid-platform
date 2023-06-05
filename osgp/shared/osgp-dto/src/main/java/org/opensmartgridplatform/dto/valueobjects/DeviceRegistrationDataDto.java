// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class DeviceRegistrationDataDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -5264884250167476931L;

  private String ipAddress;
  private String deviceType;
  private boolean hasSchedule;

  public DeviceRegistrationDataDto(
      final String ipAddress, final String deviceType, final boolean hasSchedule) {
    this.ipAddress = ipAddress;
    this.deviceType = deviceType;
    this.hasSchedule = hasSchedule;
  }

  public String getIpAddress() {
    return this.ipAddress;
  }

  public String getDeviceType() {
    return this.deviceType;
  }

  public boolean isHasSchedule() {
    return this.hasSchedule;
  }
}
