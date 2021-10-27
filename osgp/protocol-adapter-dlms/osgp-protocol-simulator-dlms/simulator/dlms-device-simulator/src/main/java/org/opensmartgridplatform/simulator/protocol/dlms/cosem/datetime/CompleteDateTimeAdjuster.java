/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.datetime;

import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;

/**
 * Applies adjustments to the given {@link Temporal temporal} for all bytes of the given date-time
 * value.
 */
public class CompleteDateTimeAdjuster extends CosemDateTimeAdjuster {

  public CompleteDateTimeAdjuster(final byte[] dateTime) {
    super(dateTime);
  }

  @Override
  public Temporal adjustInto(final Temporal temporal) {
    return temporal
        .with(new YearAdjuster(this.dateTime))
        .with(new WhenSpecifiedAdjuster(this.dateTime[2], ChronoField.MONTH_OF_YEAR))
        .with(new DayOfMonthAdjuster(this.dateTime))
        .with(new DayOfWeekAdjuster(this.dateTime))
        .with(new WhenSpecifiedAdjuster(this.dateTime[5], ChronoField.HOUR_OF_DAY))
        .with(new WhenSpecifiedAdjuster(this.dateTime[6], ChronoField.MINUTE_OF_HOUR))
        .with(new WhenSpecifiedAdjuster(this.dateTime[7], ChronoField.SECOND_OF_MINUTE))
        .with(new HundredthsAdjuster(this.dateTime))
        .with(new DeviationAdjuster(this.dateTime));
  }
}
