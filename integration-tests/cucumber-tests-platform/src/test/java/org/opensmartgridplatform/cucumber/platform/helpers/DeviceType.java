/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
