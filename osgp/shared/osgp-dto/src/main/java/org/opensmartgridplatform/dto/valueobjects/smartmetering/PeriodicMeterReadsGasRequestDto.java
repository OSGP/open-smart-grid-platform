// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Date;

/** request periodic reads for GAS meter */
public class PeriodicMeterReadsGasRequestDto extends PeriodicMeterReadsRequestDataDto {

  private static final long serialVersionUID = -2483665562035897062L;

  private final ChannelDto channel;

  public PeriodicMeterReadsGasRequestDto(
      final PeriodTypeDto periodType,
      final Date beginDate,
      final Date endDate,
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
