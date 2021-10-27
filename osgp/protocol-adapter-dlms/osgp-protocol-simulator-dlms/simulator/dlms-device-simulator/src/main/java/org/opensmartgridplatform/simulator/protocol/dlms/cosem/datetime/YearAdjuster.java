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

public class YearAdjuster extends CosemDateTimeAdjuster {

  public YearAdjuster(final byte[] dateTime) {
    super(dateTime);
  }

  @Override
  public Temporal adjustInto(final Temporal temporal) {
    LocalDateTime local = LocalDateTime.from(temporal);

    int year = this.dateTime[0] << 8;
    year |= this.dateTime[1] & 0xff;
    if ((year & 0xFFFF) != 0xFFFF) {
      local = local.withYear(year);
    }

    return local;
  }
}
