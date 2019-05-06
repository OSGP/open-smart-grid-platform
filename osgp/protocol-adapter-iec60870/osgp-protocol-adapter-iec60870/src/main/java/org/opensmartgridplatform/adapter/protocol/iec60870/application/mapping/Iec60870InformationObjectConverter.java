/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.j60870.IeQuality;
import org.openmuc.j60870.IeShortFloat;
import org.openmuc.j60870.IeTime56;
import org.openmuc.j60870.InformationElement;
import org.openmuc.j60870.InformationObject;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class Iec60870InformationObjectConverter extends CustomConverter<InformationObject, MeasurementGroupDto> {

    @Override
    public MeasurementGroupDto convert(final InformationObject source,
            final Type<? extends MeasurementGroupDto> destinationType, final MappingContext mappingContext) {

        final String identification = String.valueOf(source.getInformationObjectAddress());
        final List<MeasurementDto> measurements = new ArrayList<>();

        for (final InformationElement[] ieArray : source.getInformationElements()) {
            measurements.add(this.convert(ieArray));
        }

        return new MeasurementGroupDto(identification, measurements);
    }

    private MeasurementDto convert(final InformationElement[] source) {
        final List<MeasurementElementDto> elements = new ArrayList<>();
        for (final InformationElement ie : source) {
            if (ie instanceof IeShortFloat) {
                elements.add(this.mapperFacade.map(ie, FloatMeasurementElementDto.class));
            } else if (ie instanceof IeQuality) {
                elements.add(this.mapperFacade.map(ie, BitmaskMeasurementElementDto.class));
            } else if (ie instanceof IeTime56) {
                elements.add(this.mapperFacade.map(ie, TimestampMeasurementElementDto.class));
            }
        }
        return new MeasurementDto(elements);
    }
}
