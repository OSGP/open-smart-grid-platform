// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Date;

/** request periodic reads for E meter */
public class PeriodicMeterReadsRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = -2483665562035897062L;

  private final PeriodTypeDto periodType;
  private final Date beginDate;
  private final Date endDate;

  public PeriodicMeterReadsRequestDataDto(
      final PeriodTypeDto periodType, final Date beginDate, final Date endDate) {
    this.periodType = periodType;
    this.beginDate = new Date(beginDate.getTime());
    this.endDate = new Date(endDate.getTime());
  }

  public PeriodTypeDto getPeriodType() {
    return this.periodType;
  }

  public Date getBeginDate() {
    return new Date(this.beginDate.getTime());
  }

  public Date getEndDate() {
    return new Date(this.endDate.getTime());
  }
}
