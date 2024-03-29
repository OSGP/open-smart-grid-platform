// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.web.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DelayRequest {
  private final Integer delay;

  @JsonCreator
  public DelayRequest(@JsonProperty("delay") final Integer delay) {
    this.delay = delay;
  }

  public Integer getDelay() {
    return this.delay;
  }

  @Override
  public String toString() {
    return String.format("DelayRequest[delay=%s]", this.delay);
  }
}
