// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
