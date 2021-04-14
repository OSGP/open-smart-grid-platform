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
