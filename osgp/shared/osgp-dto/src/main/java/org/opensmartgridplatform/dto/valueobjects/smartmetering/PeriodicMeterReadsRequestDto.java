// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Date;

/**
 * request periodic reads for E or GAS meter
 *
 * @author dev
 */
public class PeriodicMeterReadsRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = -2483665562035897062L;

  private final PeriodTypeDto periodType;
  private final Date beginDate;
  private final Date endDate;
  private final ChannelDto channel;

  public PeriodicMeterReadsRequestDto(
      final PeriodTypeDto periodType,
      final Date beginDate,
      final Date endDate,
      final ChannelDto channel) {
    this.periodType = periodType;
    this.beginDate = new Date(beginDate.getTime());
    this.endDate = new Date(endDate.getTime());
    this.channel = channel;
  }

  public PeriodicMeterReadsRequestDto(
      final PeriodTypeDto periodType, final Date beginDate, final Date endDate) {
    this(periodType, beginDate, endDate, null);
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

  public boolean isMbusQuery() {
    return this.channel != null;
  }

  public ChannelDto getChannel() {
    return this.channel;
  }
}
