/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import java.util.HashMap;
import java.util.Map;

import org.opensmartgridplatform.dto.valueobjects.DaliConfigurationDto;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.Oslp.IndexAddressMap;
import org.opensmartgridplatform.oslp.Oslp.RelayType;
import com.google.protobuf.ByteString;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class DaliConfigurationToOslpDaliConfigurationConverter
        extends BidirectionalConverter<DaliConfigurationDto, Oslp.DaliConfiguration> {
    @Override
    public org.opensmartgridplatform.oslp.Oslp.DaliConfiguration convertTo(final DaliConfigurationDto source,
            final Type<org.opensmartgridplatform.oslp.Oslp.DaliConfiguration> destinationType, final MappingContext context) {
        final Oslp.DaliConfiguration.Builder daliConfiguration = Oslp.DaliConfiguration.newBuilder();

        if (source == null) {
            return null;
        }

        if (source.getNumberOfLights() != null) {
            daliConfiguration.setNumberOfLights(this.mapperFacade.map(source.getNumberOfLights(), ByteString.class));
        }

        if (source.getIndexAddressMap() != null) {
            // Not very pretty, this could (presumably) be cleaned up by proper
            // use of Orika.
            for (final Map.Entry<Integer, Integer> entry : source.getIndexAddressMap().entrySet()) {
                daliConfiguration.addAddressMap(Oslp.IndexAddressMap.newBuilder()
                        .setIndex(this.mapperFacade.map(entry.getKey(), ByteString.class))
                        .setAddress(this.mapperFacade.map(entry.getValue(), ByteString.class))
                        .setRelayType(RelayType.RT_NOT_SET));
            }
        }

        return daliConfiguration.build();
    }

    @Override
    public DaliConfigurationDto convertFrom(final org.opensmartgridplatform.oslp.Oslp.DaliConfiguration source,
            final Type<DaliConfigurationDto> destinationType, final MappingContext context) {

        if (source == null) {
            return null;
        }

        final Map<Integer, Integer> indexAddressMap = new HashMap<>();
        for (final IndexAddressMap entry : source.getAddressMapList()) {
            indexAddressMap.put(this.mapperFacade.map(entry.getIndex(), Integer.class),
                    this.mapperFacade.map(entry.getAddress(), Integer.class));
        }

        final Integer numberOfLights = Integer.valueOf(0);

        final Map<Integer, Integer> adresMap = new HashMap<>();

        return new DaliConfigurationDto(source.hasNumberOfLights()
                ? this.mapperFacade.map(source.getNumberOfLights(), Integer.class) : numberOfLights,
                !indexAddressMap.isEmpty() ? indexAddressMap : adresMap);
    }
}
