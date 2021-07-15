/*
 * 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.measurements.elements;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

public class TimestampMeasurementElementTest {

  @Test
  public void AsZonedDateTimeShouldReturnCorrectZonedDateTimeWhenConstructedUsingTimestamp() {
    // Arrange
    final ZonedDateTime expected = ZonedDateTime.of(2020, 2, 20, 21, 59, 59, 0, ZoneOffset.UTC);
    final long timestamp = expected.toInstant().toEpochMilli();

    // Act
    final TimestampMeasurementElement element = new TimestampMeasurementElement(timestamp);
    final ZonedDateTime actual = element.asZonedDateTime();

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void AsZonedDateTimeShouldReturnCorrectZonedDateTimeWhenConstructedUsingZonedDateTime() {
    // Arrange
    final ZonedDateTime expected = ZonedDateTime.of(2020, 2, 20, 21, 59, 59, 0, ZoneOffset.UTC);

    // Act
    final TimestampMeasurementElement element = new TimestampMeasurementElement(expected);
    final ZonedDateTime actual = element.asZonedDateTime();

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void getValueShouldReturnCorrectValueWhenConstructedUsingTimestamp() {
    // Arrange
    final ZonedDateTime zonedDateTime =
        ZonedDateTime.of(2020, 2, 20, 21, 59, 59, 0, ZoneOffset.UTC);
    final long expected = zonedDateTime.toInstant().toEpochMilli();

    // Act
    final TimestampMeasurementElement element = new TimestampMeasurementElement(expected);
    final long actual = element.getValue();

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void getValueShouldReturnCorrectValueWhenConstructedUsingZonedDateTime() {
    // Arrange
    final ZonedDateTime zonedDateTime =
        ZonedDateTime.of(2020, 2, 20, 21, 59, 59, 0, ZoneOffset.UTC);
    final long expected = zonedDateTime.toInstant().toEpochMilli();

    // Act
    final TimestampMeasurementElement element = new TimestampMeasurementElement(zonedDateTime);
    final long actual = element.getValue();

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
