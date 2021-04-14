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
