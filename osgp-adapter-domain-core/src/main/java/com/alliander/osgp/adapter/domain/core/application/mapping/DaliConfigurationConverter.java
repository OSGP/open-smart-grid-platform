package com.alliander.osgp.adapter.domain.core.application.mapping;

import java.util.Map;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.DaliConfiguration;

public class DaliConfigurationConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.DaliConfiguration, DaliConfiguration> {

    @Override
    public DaliConfiguration convertTo(final com.alliander.osgp.dto.valueobjects.DaliConfiguration source,
            final Type<DaliConfiguration> destinationType) {

        final Integer numberOfLights = source.getNumberOfLights();
        final Map<Integer, Integer> indexAddressMap = source.getIndexAddressMap();

        return new DaliConfiguration(numberOfLights, indexAddressMap);
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.DaliConfiguration convertFrom(final DaliConfiguration source,
            final Type<com.alliander.osgp.dto.valueobjects.DaliConfiguration> destinationType) {

        final Integer numberOfLights = source.getNumberOfLights();
        final Map<Integer, Integer> indexAddressMap = source.getIndexAddressMap();

        return new com.alliander.osgp.dto.valueobjects.DaliConfiguration(numberOfLights, indexAddressMap);
    }
}
