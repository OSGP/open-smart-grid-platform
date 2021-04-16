/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeShortFloat;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportHeaderDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;

public class Iec60870AsduConverterTest {
  private final Iec60870Mapper mapper = new Iec60870Mapper();

  private static final long TIMESTAMP_NOW =
      ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();

  private static final MeasurementReportDto MEASUREMENT_REPORT_DTO =
      new MeasurementReportDto(
          new MeasurementReportHeaderDto("M_ME_TF_1", "SPONTANEOUS", 1, 2),
          Arrays.asList(
              new MeasurementGroupDto(
                  "1",
                  Arrays.asList(
                      new MeasurementDto(
                          Arrays.asList(
                              new FloatMeasurementElementDto(10.0f),
                              new BitmaskMeasurementElementDto((byte) 0),
                              new TimestampMeasurementElementDto(TIMESTAMP_NOW)))))));

  private static final ASdu ASDU =
      new ASdu(
          ASduType.M_ME_TF_1,
          false,
          CauseOfTransmission.SPONTANEOUS,
          false,
          false,
          1,
          2,
          new InformationObject[] {
            new InformationObject(
                1,
                new InformationElement[][] {
                  {
                    new IeShortFloat(10.0f),
                    new IeQuality(false, false, false, false, false),
                    new IeTime56(TIMESTAMP_NOW)
                  }
                })
          });

  @Test
  public void shouldConvertAsduToMeasurementReportDto() {
    // Arrange
    final MeasurementReportDto expected = MEASUREMENT_REPORT_DTO;
    final ASdu source = ASDU;

    // Act
    final MeasurementReportDto actual = this.mapper.map(source, MeasurementReportDto.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
