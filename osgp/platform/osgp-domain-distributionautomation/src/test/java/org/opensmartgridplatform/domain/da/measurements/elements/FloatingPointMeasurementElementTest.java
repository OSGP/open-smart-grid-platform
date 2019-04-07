/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.domain.da.measurements.elements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.Test;

public class FloatingPointMeasurementElementTest {

    @Test
    public void getValueShouldReturnCorrectValue() {
        // Arrange
        final float expected = 10.0f;

        // Act
        final FloatingPointMeasurementElement element = new FloatingPointMeasurementElement(expected);
        final float actual = element.getValue();

        // Assert
        assertThat(actual).isCloseTo(expected, within(0.001f));
    }
}
