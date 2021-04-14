/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.openmuc.j60870.ie.IeTime56;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;

public class IeTime56ConverterTest {

  private final IeTime56Converter converter = new IeTime56Converter();

  @Test
  public void shouldConvertIeTime56ToTimestamp() {
    // Arrange
    final long timestamp = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
    final TimestampMeasurementElementDto expected = new TimestampMeasurementElementDto(timestamp);
    final IeTime56 source = new IeTime56(timestamp);

    // Act
    final TimestampMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
