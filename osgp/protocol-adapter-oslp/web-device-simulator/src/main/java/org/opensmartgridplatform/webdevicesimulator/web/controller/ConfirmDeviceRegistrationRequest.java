//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.webdevicesimulator.web.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfirmDeviceRegistrationRequest {
  private Long deviceId;

  public ConfirmDeviceRegistrationRequest(@JsonProperty("deviceId") final Long deviceId) {
    this.deviceId = deviceId;
  }

  public Long getDeviceId() {
    return this.deviceId;
  }
}
