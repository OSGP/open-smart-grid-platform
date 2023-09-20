// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.time.Instant;

/** request periodic reads for GAS meter */
public class PeriodicMeterReadsGasRequestDto extends PeriodicMeterReadsRequestDataDto {

  private static final long serialVersionUID = -2483665562035897062L;

  private final ChannelDto channel;

  public PeriodicMeterReadsGasRequestDto(
      final PeriodTypeDto periodType,
      final Instant beginDate,
      final Instant endDate,
      final ChannelDto channel) {
    super(periodType, beginDate, endDate);
    this.channel = channel;
  }

  public boolean isMbusQuery() {
    return this.channel != null;
  }

  public ChannelDto getChannel() {
    return this.channel;
  }
}
