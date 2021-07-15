/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
