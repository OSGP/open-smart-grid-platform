/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.measurements.elements;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;
import org.opensmartgridplatform.domain.da.measurements.MeasurementElement;

/** Stores a timestamp as the number of milliseconds since the epoch of 1970-01-01T00:00:00Z. */
public class TimestampMeasurementElement implements MeasurementElement {

  private static final long serialVersionUID = 1L;

  private final Long value;

  public TimestampMeasurementElement(final long value) {
    this.value = value;
  }

  public TimestampMeasurementElement(final ZonedDateTime zonedDateTime) {
    this(zonedDateTime.toInstant().toEpochMilli());
  }

  public Long getValue() {
    return this.value;
  }

  public ZonedDateTime asZonedDateTime() {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.value), ZoneOffset.UTC);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TimestampMeasurementElement)) {
      return false;
    }
    final TimestampMeasurementElement that = (TimestampMeasurementElement) obj;
    return Objects.equals(this.value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }

  @Override
  public String toString() {
    return "Timestamp: " + this.value;
  }
}
