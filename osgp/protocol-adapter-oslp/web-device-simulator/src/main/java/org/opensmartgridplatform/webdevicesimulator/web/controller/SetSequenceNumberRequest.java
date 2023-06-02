//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.webdevicesimulator.web.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SetSequenceNumberRequest {
  private Long deviceId;
  private Integer sequenceNumber;

  @JsonCreator
  public SetSequenceNumberRequest(
      @JsonProperty("deviceId") final Long deviceId,
      @JsonProperty("sequenceNumber") final Integer sequenceNumber) {
    this.deviceId = deviceId;
    this.sequenceNumber = sequenceNumber;
  }

  public Long getDeviceId() {
    return this.deviceId;
  }

  public Integer getSequenceNumber() {
    return this.sequenceNumber;
  }
}
