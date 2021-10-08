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

public class HundredthsAdjuster extends CosemDateTimeAdjuster {

  private static final int HUNDREDTHS_TO_NANO_CONVERSION = 10000000;

  public HundredthsAdjuster(final byte[] dateTime) {
    super(dateTime);
  }

  @Override
  public Temporal adjustInto(final Temporal temporal) {
    LocalDateTime local = LocalDateTime.from(temporal);

    if (this.dateTime[8] != (byte) 0xFF) {
      local = local.withNano(this.dateTime[8] / HUNDREDTHS_TO_NANO_CONVERSION);
    }

    return local;
  }
}
