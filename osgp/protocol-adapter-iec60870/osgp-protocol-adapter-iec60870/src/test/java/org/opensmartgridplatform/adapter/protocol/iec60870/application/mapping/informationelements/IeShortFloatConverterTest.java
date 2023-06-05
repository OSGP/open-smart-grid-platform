// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
