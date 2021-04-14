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

import org.junit.jupiter.api.Test;
import org.openmuc.j60870.ie.IeShortFloat;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;

public class IeShortFloatConverterTest {

  private final IeShortFloatConverter converter = new IeShortFloatConverter();

  @Test
  public void shouldConvertIeShortFloatToFloatMeasurementElementDto() {
    // Arrange
    final float value = 10.0f;
    final FloatMeasurementElementDto expected = new FloatMeasurementElementDto(value);
    final IeShortFloat source = new IeShortFloat(value);

    // Act
    final FloatMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
