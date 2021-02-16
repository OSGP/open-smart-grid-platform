/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualValueDto;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class ActualPowerQualityDtoConverter
        extends CustomConverter<ActualPowerQualityResponseDto, ActualPowerQualityResponse> {

    private final MapperFactory mapperFactory;

    public ActualPowerQualityDtoConverter(final MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;
    }

    @Override
    public ActualPowerQualityResponse convert(final ActualPowerQualityResponseDto source,
            final Type<? extends ActualPowerQualityResponse> destinationType, final MappingContext mappingContext) {

        final ActualPowerQualityResponse response = new ActualPowerQualityResponse();

        if (source.getActualPowerQualityData() != null) {
            final ActualPowerQualityDataDto responseDataDto = source.getActualPowerQualityData();

            final List<CaptureObject> captureObjects = new ArrayList<>(
                    this.mapperFacade.mapAsList(responseDataDto.getCaptureObjects(), CaptureObject.class));

            final List<ActualValue> actualValues = this.makeActualValues(responseDataDto);

            final ActualPowerQualityData actualPowerQualityData = new ActualPowerQualityData(captureObjects,
                    actualValues);
            response.setActualPowerQualityData(actualPowerQualityData);
        }
        return response;
    }

    private List<ActualValue> makeActualValues(final ActualPowerQualityDataDto responseDataDto) {
        final List<ActualValue> actualValues = new ArrayList<>();

        for (final ActualValueDto actualValueDto : responseDataDto.getActualValues()) {
            final ActualValue actualValue = this.mapperFactory.getMapperFacade().map(actualValueDto, ActualValue.class);
            actualValues.add(actualValue);
        }

        return actualValues;
    }
}
