// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.time.Instant;

/** request periodic reads for E meter */
public class PeriodicMeterReadsRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = -2483665562035897062L;

  private final PeriodTypeDto periodType;
  private final Instant beginDate;
  private final Instant endDate;

  public PeriodicMeterReadsRequestDataDto(
      final PeriodTypeDto periodType, final Instant beginDate, final Instant endDate) {
    this.periodType = periodType;
    this.beginDate = beginDate;
    this.endDate = endDate;
  }

  public PeriodTypeDto getPeriodType() {
    return this.periodType;
  }

  public Instant getBeginDate() {
    return this.beginDate;
  }

  public Instant getEndDate() {
    return this.endDate;
  }
}
