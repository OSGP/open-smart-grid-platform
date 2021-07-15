/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Date;

public class MeterReadsGasResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -156966569210717654L;

  private final Date logTime;
  private final Date captureTime;
  private final DlmsMeterValueDto consumption;

  public MeterReadsGasResponseDto(
      final Date logTime, final DlmsMeterValueDto consumption, final Date captureTime) {
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

  public DlmsMeterValueDto getConsumption() {
    return this.consumption;
  }

  @Override
  public String toString() {
    return "MeterReadsGas [logTime="
        + this.logTime
        + ", captureTime="
        + this.captureTime
        + ", consumption="
        + this.consumption
        + "]";
  }
}
