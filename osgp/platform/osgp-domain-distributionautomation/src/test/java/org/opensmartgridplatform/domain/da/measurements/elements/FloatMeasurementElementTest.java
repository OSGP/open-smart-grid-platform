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
