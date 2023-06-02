//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;

public class IeSinglePointWithQualityConverterTest {

  private static final int IE_OFF = 0b00000000;
  private static final int IE_ON = 0b00000001;
  private static final int IE_QUALITY_BLOCKED = 0b00010000;
  private static final int IE_QUALITY_SUBSTITUTED = 0b00100000;
  private static final int IE_QUALITY_NOT_TOPICAL = 0b01000000;
  private static final int IE_QUALITY_INVALID = 0b10000000;
  private static final int IE_ALL =
      IE_ON
          | IE_QUALITY_BLOCKED
          | IE_QUALITY_SUBSTITUTED
          | IE_QUALITY_NOT_TOPICAL
          | IE_QUALITY_INVALID;

  private final IeSinglePointWithQualityConverter converter =
      new IeSinglePointWithQualityConverter();

  @Test
  public void testConvertIeSinglePointWithQualityOff() {
    // Arrange
    final BitmaskMeasurementElementDto expected = new BitmaskMeasurementElementDto((byte) IE_OFF);
    final IeSinglePointWithQuality source =
        new IeSinglePointWithQuality(false, false, false, false, false);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testConvertIeSinglePointWithQualityOn() {
    // Arrange
    final BitmaskMeasurementElementDto expected = new BitmaskMeasurementElementDto((byte) IE_ON);
    final IeSinglePointWithQuality source =
        new IeSinglePointWithQuality(true, false, false, false, false);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testConvertIeSinglePointWithQualityBlocked() {
    // Arrange
    final BitmaskMeasurementElementDto expected =
        new BitmaskMeasurementElementDto((byte) IE_QUALITY_BLOCKED);
    final IeSinglePointWithQuality source =
        new IeSinglePointWithQuality(false, true, false, false, false);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testConvertIeSinglePointWithQualitySubstituted() {
    // Arrange
    final BitmaskMeasurementElementDto expected =
        new BitmaskMeasurementElementDto((byte) IE_QUALITY_SUBSTITUTED);
    final IeSinglePointWithQuality source =
        new IeSinglePointWithQuality(false, false, true, false, false);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testConvertIeSinglePointWithQualityNotTopical() {
    // Arrange
    final BitmaskMeasurementElementDto expected =
        new BitmaskMeasurementElementDto((byte) IE_QUALITY_NOT_TOPICAL);
    final IeSinglePointWithQuality source =
        new IeSinglePointWithQuality(false, false, false, true, false);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testConvertIeSinglePointWithQualityInvalid() {
    // Arrange
    final BitmaskMeasurementElementDto expected =
        new BitmaskMeasurementElementDto((byte) IE_QUALITY_INVALID);
    final IeSinglePointWithQuality source =
        new IeSinglePointWithQuality(false, false, false, false, true);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testConvertIeSinglePointWithQualityAll() {
    // Arrange
    final BitmaskMeasurementElementDto expected = new BitmaskMeasurementElementDto((byte) IE_ALL);
    final IeSinglePointWithQuality source =
        new IeSinglePointWithQuality(true, true, true, true, true);

    // Act
    final BitmaskMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
