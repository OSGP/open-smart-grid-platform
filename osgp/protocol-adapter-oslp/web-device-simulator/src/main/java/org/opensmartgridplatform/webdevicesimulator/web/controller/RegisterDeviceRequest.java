// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.web.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** JSon register device request */
public class RegisterDeviceRequest {
  private Long deviceId;
  private String deviceType;
  private boolean hasSchedule;

  /** Constructor */
  @JsonCreator
  public RegisterDeviceRequest(
      @JsonProperty("deviceId") final Long deviceId,
      @JsonProperty("deviceType") final String deviceType,
      @JsonProperty("hasSchedule") final String hasSchedule) {
    this.deviceId = deviceId;
    this.deviceType = deviceType;
    this.hasSchedule = "on".equals(hasSchedule);
  }

  public Long getDeviceId() {
    return this.deviceId;
  }

  public String getDeviceType() {
    return this.deviceType;
  }

  public boolean getHasSchedule() {
    return this.hasSchedule;
  }
}
