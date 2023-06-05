// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.web.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetSequenceNumberRequest {
  private Long deviceId;

  @JsonCreator
  public GetSequenceNumberRequest(@JsonProperty("deviceId") final Long deviceId) {
    this.deviceId = deviceId;
  }

  public Long getDeviceId() {
    return this.deviceId;
  }
}
