/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.Test;
import org.openmuc.j60870.IeQuality;
import org.openmuc.j60870.IeShortFloat;
import org.openmuc.j60870.IeTime56;
import org.openmuc.j60870.InformationElement;
import org.openmuc.j60870.InformationObject;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;

public class Iec60870InformationObjectConverterTest {

    private final Iec60870Mapper mapper = new Iec60870Mapper();

    private static final long timestamp = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();

    // @formatter:off
    private static final MeasurementGroupDto MEASUREMENT_GROUP_DTO = new MeasurementGroupDto(
            "1",
            Arrays.asList(new MeasurementDto(Arrays.asList(
                    new FloatMeasurementElementDto(10.0f),
                    new BitmaskMeasurementElementDto((byte) 0),
                    new TimestampMeasurementElementDto(timestamp)))));

    private static final InformationObject INFORMATION_OBJECT = new InformationObject(
            1,
            new InformationElement[][] { {
                    new IeShortFloat(10.0f),
                    new IeQuality(false, false, false, false, false),
                    new IeTime56(timestamp) } });
    // @formatter:on

    @Test
    public void shouldConvertInformationObjectToMeasurementGroupDto() {
        // Arrange
        final MeasurementGroupDto expected = MEASUREMENT_GROUP_DTO;
        final InformationObject source = INFORMATION_OBJECT;

        // Act
        final MeasurementGroupDto actual = this.mapper.map(source, MeasurementGroupDto.class);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
