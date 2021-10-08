/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.datetime;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;

/**
 * Adjust the {@link Temporal temporal} to set the Day of Week value from the given date time byte
 * array. Day of Week can be interpreted together with Day of Month. Always be sure to apply {@link
 * DayOfMonthAdjuster DayOfMonthAdjuster} before this adjuster to adhere to the rules of Blue book.
 *
 * <p>See Blue book Ed. 12, pages 42 and 43 for more information.
 */
public class DayOfWeekAdjuster extends CosemDateTimeAdjuster {

  public DayOfWeekAdjuster(final byte[] dateTime) {
    super(dateTime);
  }

  @Override
  public Temporal adjustInto(final Temporal temporal) {
    LocalDateTime local = LocalDateTime.from(temporal);

    if (this.dateTime[4] != (byte) 0xFF) {
      if (this.dateTime[3] == (byte) 0xFE || this.dateTime[3] == (byte) 0xFD) {
        local = local.with(TemporalAdjusters.lastInMonth(java.time.DayOfWeek.of(this.dateTime[4])));
        if (this.dateTime[3] == (byte) 0xFD) {
          local = local.minusWeeks(1L);
        }
      } else {
        local = local.with(java.time.DayOfWeek.of(this.dateTime[4]));
      }
    }

    return local;
  }
}
