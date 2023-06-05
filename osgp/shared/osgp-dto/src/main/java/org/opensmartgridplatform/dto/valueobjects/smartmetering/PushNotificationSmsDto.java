// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class PushNotificationSmsDto implements Serializable {

  private static final long serialVersionUID = -5389008513362783376L;

  private final String deviceIdentification;
  private final String ipAddress;

  public PushNotificationSmsDto(final String deviceIdentification, final String ipAddress) {
    this.deviceIdentification = deviceIdentification;
    this.ipAddress = ipAddress;
  }

  @Override
  public String toString() {
    return String.format(
        "PushNotificationSms[device=%s, ipAddress=%s]", this.deviceIdentification, this.ipAddress);
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getIpAddress() {
    return this.ipAddress;
  }
}
