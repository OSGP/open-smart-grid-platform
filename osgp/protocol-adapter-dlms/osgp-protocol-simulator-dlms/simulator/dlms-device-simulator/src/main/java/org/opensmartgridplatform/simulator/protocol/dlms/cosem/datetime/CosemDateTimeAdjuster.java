/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.datetime;

import java.time.temporal.TemporalAdjuster;

public abstract class CosemDateTimeAdjuster implements TemporalAdjuster {
  protected final byte[] dateTime;

  public CosemDateTimeAdjuster(final byte[] dateTime) {
    if (dateTime.length != 12) {
      throw new IllegalArgumentException("The Cosem Date-Time byte array must contain 12 bytes.");
    }
    this.dateTime = dateTime;
  }
}
