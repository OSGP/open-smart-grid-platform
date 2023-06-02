//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;
import org.openmuc.j60870.ie.IeTime56;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;

public class IeTime56ConverterTest {

  private final TimeZone timeZone = TimeZone.getTimeZone("UTC");

  private final IeTime56Converter converter = new IeTime56Converter(this.timeZone);

  @Test
  public void shouldConvertIeTime56ToTimestamp() {
    // Arrange
    final long timestamp = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
    final TimestampMeasurementElementDto expected = new TimestampMeasurementElementDto(timestamp);
    final IeTime56 source = new IeTime56(timestamp, this.timeZone, false);

    // Act
    final TimestampMeasurementElementDto actual = this.converter.convert(source, null, null);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
