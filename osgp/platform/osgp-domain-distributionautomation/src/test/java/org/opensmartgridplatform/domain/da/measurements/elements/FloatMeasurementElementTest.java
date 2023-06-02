//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.da.measurements.elements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

public class FloatMeasurementElementTest {

  @Test
  public void getValueShouldReturnCorrectValue() {
    // Arrange
    final float expected = 10.0f;

    // Act
    final FloatMeasurementElement element = new FloatMeasurementElement(expected);
    final float actual = element.getValue();

    // Assert
    assertThat(actual).isCloseTo(expected, within(0.001f));
  }
}
