// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.time.Instant;

/**
 * request periodic reads for E or GAS meter
 *
 * @author dev
 */
public class PeriodicMeterReadsRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = -2483665562035897062L;

  private final PeriodTypeDto periodType;
  private final Instant beginDate;
  private final Instant endDate;
  private final ChannelDto channel;

  public PeriodicMeterReadsRequestDto(
      final PeriodTypeDto periodType,
      final Instant beginDate,
      final Instant endDate,
      final ChannelDto channel) {
    this.periodType = periodType;
    this.beginDate = beginDate;
    this.endDate = beginDate;
    this.channel = channel;
  }

  public PeriodicMeterReadsRequestDto(
      final PeriodTypeDto periodType, final Instant beginDate, final Instant endDate) {
    this(periodType, beginDate, endDate, null);
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

  public boolean isMbusQuery() {
    return this.channel != null;
  }

  public ChannelDto getChannel() {
    return this.channel;
  }
}
