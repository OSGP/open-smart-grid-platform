//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.webdevicesimulator.web.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AutonomousRequest {
  private final Boolean autonomousStatus;

  @JsonCreator
  public AutonomousRequest(@JsonProperty("autonomousStatus") final Boolean autonomousStatus) {
    this.autonomousStatus = autonomousStatus;
  }

  public Boolean getAutonomousStatus() {
    return this.autonomousStatus;
  }

  @Override
  public String toString() {
    return String.format("AutonomousRequest[autonomousStatus=%s]", this.autonomousStatus);
  }
}
