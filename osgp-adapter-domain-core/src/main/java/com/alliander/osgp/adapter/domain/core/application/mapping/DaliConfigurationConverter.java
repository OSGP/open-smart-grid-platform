/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.core.application.mapping;

import java.util.Map;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.DaliConfiguration;

public class DaliConfigurationConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.DaliConfigurationDto, DaliConfiguration> {

    @Override
    public DaliConfiguration convertTo(final com.alliander.osgp.dto.valueobjects.DaliConfigurationDto source,
            final Type<DaliConfiguration> destinationType) {

        final Integer numberOfLights = source.getNumberOfLights();
        final Map<Integer, Integer> indexAddressMap = source.getIndexAddressMap();

        return new DaliConfiguration(numberOfLights, indexAddressMap);
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.DaliConfigurationDto convertFrom(final DaliConfiguration source,
            final Type<com.alliander.osgp.dto.valueobjects.DaliConfigurationDto> destinationType) {

        final Integer numberOfLights = source.getNumberOfLights();
        final Map<Integer, Integer> indexAddressMap = source.getIndexAddressMap();

        return new com.alliander.osgp.dto.valueobjects.DaliConfigurationDto(numberOfLights, indexAddressMap);
    }
}
