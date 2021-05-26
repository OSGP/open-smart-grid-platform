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

import java.util.Arrays;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeShortFloat;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;

public class Iec60870InformationObjectConverterTest {

  private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Europe/Paris");
  private final Iec60870Mapper mapper = new Iec60870Mapper(TIME_ZONE);

  private static final long TIMESTAMP_NOW = System.currentTimeMillis();

  private static final InformationElement IE_1 = new IeQuality(true, true, true, true, true);
  private static final InformationElement IE_2 = new IeShortFloat(30.51f);
  private static final InformationElement IE_3 =
      new IeSinglePointWithQuality(true, false, false, false, false);
  private static final InformationElement IE_4 = new IeTime56(TIMESTAMP_NOW, TIME_ZONE, false);

  private static final MeasurementElementDto ME_1 = new BitmaskMeasurementElementDto((byte) 241);
  private static final MeasurementElementDto ME_2 = new FloatMeasurementElementDto(30.51f);
  private static final MeasurementElementDto ME_3 = new BitmaskMeasurementElementDto((byte) 1);
  private static final MeasurementElementDto ME_4 =
      new TimestampMeasurementElementDto(TIMESTAMP_NOW);

  private static final MeasurementGroupDto MEASUREMENT_GROUP_DTO =
      new MeasurementGroupDto(
          "1", Arrays.asList(new MeasurementDto(Arrays.asList(ME_1, ME_2, ME_3, ME_4))));

  private static final InformationObject INFORMATION_OBJECT =
      new InformationObject(1, new InformationElement[][] {{IE_1, IE_2, IE_3, IE_4}});

  @Test
  public void testConvertInformationObjectToMeasurementGroupDto() {
    // Arrange
    final MeasurementGroupDto expected = MEASUREMENT_GROUP_DTO;
    final InformationObject source = INFORMATION_OBJECT;

    // Act
    final MeasurementGroupDto actual = this.mapper.map(source, MeasurementGroupDto.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
