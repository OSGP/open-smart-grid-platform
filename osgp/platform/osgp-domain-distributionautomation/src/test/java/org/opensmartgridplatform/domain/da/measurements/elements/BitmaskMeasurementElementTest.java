/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.measurements.elements;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.da.measurements.elements.BitmaskMeasurementElement.BitmaskFlag;

public class BitmaskMeasurementElementTest {

  @Test
  public void asEnumSetShouldReturnCorrectEnumSetForAllFlagsSet() {
    // Arrange
    final Byte bitmask = (byte) 255;
    final BitmaskMeasurementElement element = new BitmaskMeasurementElement(bitmask);
    final EnumSet<BitmaskFlag> expected = EnumSet.allOf(BitmaskFlag.class);

    // Act
    final Set<BitmaskFlag> actual = element.asEnumSet();

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
    final Set<BitmaskFlag> actual = element.asEnumSet();

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
    final Set<BitmaskFlag> actual = element.asEnumSet();

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
    final Set<BitmaskFlag> actual = element.asEnumSet();

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
    final boolean actual = element.getFlag(BitmaskFlag.FLAG_1);

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
    final boolean actual = element.getFlag(BitmaskFlag.FLAG_1);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

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
}
