// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.helpers;

public enum DeviceType {
  DISTRIBUTION_AUTOMATION_DEVICE("RTU", "DISTRIBUTION_AUTOMATION_DEVICE"),
  LIGHT_MEASUREMENT_DEVICE("LMD", "LIGHT_SENSOR"),
  LIGHT_MEASUREMENT_RTU("RTU", "LIGHT_MEASUREMENT_RTU");

  private String platformDeviceType;
  private String protocolDeviceType;

  DeviceType(final String platformDeviceType, final String protocolDeviceType) {
    this.platformDeviceType = platformDeviceType;
    this.protocolDeviceType = protocolDeviceType;
  }

  public String getPlatformDeviceType() {
    return this.platformDeviceType;
  }

  public String getProtocolDeviceType() {
    return this.protocolDeviceType;
  }
}
