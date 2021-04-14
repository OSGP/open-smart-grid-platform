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
import org.openmuc.j60870.ie.IeQuality;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;

public class IeQualityConverterTest {

  private static final int IE_QUALITY_NONE = 0b00000000;
  private static final int IE_QUALITY_OVERFLOW = 0b00000001;
  private static final int IE_QUALITY_BLOCKED = 0b00010000;
  private static final int IE_QUALITY_SUBSTITUTED = 0b00100000;
  private static final int IE_QUALITY_NOT_TOPICAL = 0b01000000;
  private static final int IE_QUALITY_INVALID = 0b10000000;

  private static final int IE_QUALITY_ALL =
      IE_QUALITY_OVERFLOW
          | IE_QUALITY_BLOCKED
          | IE_QUALITY_SUBSTITUTED
          | IE_QUALITY_NOT_TOPICAL
          | IE_QUALITY_INVALID;

  private final IeQualityConverter converter = new IeQualityConverter();

  @Test
  public void shouldConvertIeQualityNoneToByte() {
    // Arrange
    final BitmaskMeasurementElementDto expected =
        new BitmaskMeasurementElementDto((byte) IE_QUALITY_NONE);
    final IeQuality source = new IeQuality(false, false, false, false, false);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void shouldConvertIeQualityOverflowToByte() {
    // Arrange
    final BitmaskMeasurementElementDto expected =
        new BitmaskMeasurementElementDto((byte) IE_QUALITY_OVERFLOW);
    final IeQuality source = new IeQuality(true, false, false, false, false);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void shouldConvertIeQualityBlockedToByte() {
    // Arrange
    final BitmaskMeasurementElementDto expected =
        new BitmaskMeasurementElementDto((byte) IE_QUALITY_BLOCKED);
    final IeQuality source = new IeQuality(false, true, false, false, false);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void shouldConvertIeQualitySubstitutedToByte() {
    // Arrange
    final BitmaskMeasurementElementDto expected =
        new BitmaskMeasurementElementDto((byte) IE_QUALITY_SUBSTITUTED);
    final IeQuality source = new IeQuality(false, false, true, false, false);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void shouldConvertIeQualityNotTopicalToByte() {
    // Arrange
    final BitmaskMeasurementElementDto expected =
        new BitmaskMeasurementElementDto((byte) IE_QUALITY_NOT_TOPICAL);
    final IeQuality source = new IeQuality(false, false, false, true, false);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void shouldConvertIeQualityInvalidToByte() {
    // Arrange
    final BitmaskMeasurementElementDto expected =
        new BitmaskMeasurementElementDto((byte) IE_QUALITY_INVALID);
    final IeQuality source = new IeQuality(false, false, false, false, true);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void shouldConvertIeQualityAllToByte() {
    // Arrange
    final BitmaskMeasurementElementDto expected =
        new BitmaskMeasurementElementDto((byte) IE_QUALITY_ALL);
    final IeQuality source = new IeQuality(true, true, true, true, true);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
