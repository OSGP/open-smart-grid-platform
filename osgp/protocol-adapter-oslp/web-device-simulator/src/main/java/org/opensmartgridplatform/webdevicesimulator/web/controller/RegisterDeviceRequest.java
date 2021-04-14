/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
