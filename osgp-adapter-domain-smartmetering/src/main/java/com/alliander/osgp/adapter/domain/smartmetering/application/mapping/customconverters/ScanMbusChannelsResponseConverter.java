/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ScanMbusChannelsResponseData;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class ScanMbusChannelsResponseConverter
        extends CustomConverter<ScanMbusChannelsResponseDto, ScanMbusChannelsResponseData> {

    @Override
    public ScanMbusChannelsResponseData convert(final ScanMbusChannelsResponseDto source,
            final Type<? extends ScanMbusChannelsResponseData> destinationType, final MappingContext mappingContext) {
        return new ScanMbusChannelsResponseData(source.getMbusIdentificationNumber1(),
                source.getMbusIdentificationNumber2(), source.getMbusIdentificationNumber3(),
                source.getMbusIdentificationNumber4());
    }

}
