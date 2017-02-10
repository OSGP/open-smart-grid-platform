/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAttributeValuesResponse;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAttributeValuesResponseDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

public class GetAttributeValuesResponseConverter
        extends CustomConverter<GetAttributeValuesResponseDto, GetAttributeValuesResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAttributeValuesResponseConverter.class);

    @Override
    public GetAttributeValuesResponse convert(final GetAttributeValuesResponseDto source,
            final Type<? extends GetAttributeValuesResponse> destinationType) {
        return new GetAttributeValuesResponse(source.getConfigurationData());

    }

}
