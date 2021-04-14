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

public class PeriodicMeterReadsGasResponseItemDto extends MeterReadsGasResponseDto {

  private static final long serialVersionUID = -3180493284656180074L;

  final AmrProfileStatusCodeDto amrProfileStatusCode;

  public PeriodicMeterReadsGasResponseItemDto(
      final Date logTime, final DlmsMeterValueDto consumption, final Date captureTime) {
    super(logTime, consumption, captureTime);
    this.amrProfileStatusCode = null;
  }

  public PeriodicMeterReadsGasResponseItemDto(
      final Date logTime,
      final DlmsMeterValueDto consumption,
      final Date captureTime,
      final AmrProfileStatusCodeDto amrProfileStatusCode) {
    super(logTime, consumption, captureTime);
    this.amrProfileStatusCode = amrProfileStatusCode;
  }

  public AmrProfileStatusCodeDto getAmrProfileStatusCode() {
    return this.amrProfileStatusCode;
  }

  @Override
  public String toString() {
    return "PeriodicMeterReadsGas [amrProfileStatusCode="
        + this.amrProfileStatusCode
        + ", getLogTime()="
        + this.getLogTime()
        + ", getCaptureTime()="
        + this.getCaptureTime()
        + ", getConsumption()="
        + this.getConsumption()
        + "]";
  }
}
