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
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;

/** Set the given field to the given value when the value is not 0xFF - 'Not specified'. */
public class WhenSpecifiedAdjuster implements TemporalAdjuster {

  private final byte value;
  private final ChronoField chronoField;

  /**
   * Constructor
   *
   * @param value Value to set when value is not 0xFF - 'Not specified'
   * @param chronoField The field in which the value will be set.
   */
  public WhenSpecifiedAdjuster(final byte value, final ChronoField chronoField) {
    this.value = value;
    this.chronoField = chronoField;
  }

  @Override
  public Temporal adjustInto(final Temporal temporal) {
    LocalDateTime local = LocalDateTime.from(temporal);
    if (this.value != (byte) 0xFF) {
      local = local.with(this.chronoField, this.value);
    }
    return local;
  }
}
