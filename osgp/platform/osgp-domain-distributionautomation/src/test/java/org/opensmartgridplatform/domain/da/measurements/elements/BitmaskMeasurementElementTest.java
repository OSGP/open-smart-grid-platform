/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.domain.da.measurements.elements;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;

import org.junit.Test;
import org.opensmartgridplatform.domain.da.measurements.elements.BitmaskMeasurementElement.BitmaskFlag;

public class BitmaskMeasurementElementTest {

    @Test
    public void getValueShouldReturnCorrectValue() {
        // Arrange
        final Byte expected = 1;
        final BitmaskMeasurementElement element = new BitmaskMeasurementElement(expected);

        // Act
        final Byte actual = element.getValue();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void asEnumSetShouldReturnCorrectEnumSetWhenNoFlagsSet() {
        // Arrange
        final Byte bitmask = 0;
        final BitmaskMeasurementElement element = new BitmaskMeasurementElement(bitmask);
        final EnumSet<BitmaskFlag> expected = EnumSet.noneOf(BitmaskFlag.class);

        // Act
        final EnumSet<BitmaskFlag> actual = element.asEnumSet();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void asEnumSetShouldReturnCorrectEnumSetWhenFlag1Set() {
        // Arrange
        final Byte bitmask = 1;
        final BitmaskMeasurementElement element = new BitmaskMeasurementElement(bitmask);
        final EnumSet<BitmaskFlag> expected = EnumSet.noneOf(BitmaskFlag.class);
        expected.add(BitmaskFlag.FLAG_1);

        // Act
        final EnumSet<BitmaskFlag> actual = element.asEnumSet();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void asEnumSetShouldReturnCorrectEnumSetForFlag8Set() {
        // Arrange
        final Byte bitmask = (byte) 128;
        final BitmaskMeasurementElement element = new BitmaskMeasurementElement(bitmask);
        final EnumSet<BitmaskFlag> expected = EnumSet.noneOf(BitmaskFlag.class);
        expected.add(BitmaskFlag.FLAG_8);

        // Act
        final EnumSet<BitmaskFlag> actual = element.asEnumSet();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void asEnumSetShouldReturnCorrectEnumSetForAllFlagsSet() {
        // Arrange
        final Byte bitmask = (byte) 255;
        final BitmaskMeasurementElement element = new BitmaskMeasurementElement(bitmask);
        final EnumSet<BitmaskFlag> expected = EnumSet.allOf(BitmaskFlag.class);

        // Act
        final EnumSet<BitmaskFlag> actual = element.asEnumSet();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getFlagShouldReturnFalseWhenFlagIsNotSet() {
        // Arrange
        final Byte bitmask = (byte) 0;
        final BitmaskMeasurementElement element = new BitmaskMeasurementElement(bitmask);
        final boolean expected = false;

        // Act
        final boolean actual = element.getFlag((short) 0);

        // Assert
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    public void getFlagShouldReturnTrueWhenFlagIsSet() {
        // Arrange
        final Byte bitmask = (byte) 1;
        final BitmaskMeasurementElement element = new BitmaskMeasurementElement(bitmask);
        final boolean expected = true;

        // Act
        final boolean actual = element.getFlag((short) 0);

        // Assert
        assertThat(actual).isEqualTo(expected);

    }
}
