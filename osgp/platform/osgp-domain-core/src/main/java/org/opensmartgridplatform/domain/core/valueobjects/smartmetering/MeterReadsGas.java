/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class MeterReadsGas extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -156966569210717654L;

  private final Date logTime;
  private final Date captureTime;
  private final OsgpMeterValue consumption;

  public MeterReadsGas(
      final Date logTime, final OsgpMeterValue consumption, final Date captureTime) {
    this.logTime = new Date(logTime.getTime());
    this.captureTime = new Date(captureTime.getTime());
    this.consumption = consumption;
  }

  public Date getLogTime() {
    return new Date(this.logTime.getTime());
  }

  public Date getCaptureTime() {
    return new Date(this.captureTime.getTime());
  }

  public OsgpMeterValue getConsumption() {
    return this.consumption;
  }
}
