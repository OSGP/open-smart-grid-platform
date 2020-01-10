/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.application.mapping;

import java.util.ArrayList;
import java.util.List;

import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportHeaderDto;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class GetDataResponseDtoToMeasurementReportDtoConverter
        extends CustomConverter<GetDataResponseDto, MeasurementReportDto> {

    @Override
    public MeasurementReportDto convert(final GetDataResponseDto source,
            final Type<? extends MeasurementReportDto> destinationType, final MappingContext mappingContext) {

        final List<MeasurementDto> measurements = new ArrayList<>();

        for (final GetDataSystemIdentifierDto id : source.getGetDataSystemIdentifiers()) {
            final List<MeasurementElementDto> measurementElements = new ArrayList<>();
            for (final org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto m : id.getMeasurements()) {
                measurementElements.add(new FloatMeasurementElementDto((float) m.getValue()));
                measurementElements.add(new TimestampMeasurementElementDto(m.getTime()
                        .getMillis()));
            }
            measurements.add(new MeasurementDto(measurementElements));
        }

        final List<MeasurementGroupDto> measurementGroups = new ArrayList<>();
        measurementGroups.add(new MeasurementGroupDto(source.getReport()
                .getId(), measurements));
        final MeasurementReportHeaderDto reportHeader = null;
        return new MeasurementReportDto(reportHeader, measurementGroups);
    }

}
