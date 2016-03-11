/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.application.mapping;

import java.util.HashMap;
import java.util.Map;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.DaliConfiguration;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.IndexAddressMap;
import com.alliander.osgp.oslp.Oslp.RelayType;
import com.google.protobuf.ByteString;

public class DaliConfigurationToOslpDaliConfigurationConverter extends
        BidirectionalConverter<DaliConfiguration, Oslp.DaliConfiguration> {
    @Override
    public com.alliander.osgp.oslp.Oslp.DaliConfiguration convertTo(final DaliConfiguration source,
            final Type<com.alliander.osgp.oslp.Oslp.DaliConfiguration> destinationType) {
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
    public DaliConfiguration convertFrom(final com.alliander.osgp.oslp.Oslp.DaliConfiguration source,
            final Type<DaliConfiguration> destinationType) {

        if (source == null) {
            return null;
        }

        final Map<Integer, Integer> indexAddressMap = new HashMap<Integer, Integer>();
        for (final IndexAddressMap entry : source.getAddressMapList()) {
            indexAddressMap.put(this.mapperFacade.map(entry.getIndex(), Integer.class),
                    this.mapperFacade.map(entry.getAddress(), Integer.class));
        }

        final Integer numberOfLights = Integer.valueOf(0);

        final Map<Integer, Integer> adresMap = new HashMap<Integer, Integer>();

        return new DaliConfiguration(source.hasNumberOfLights() ? this.mapperFacade.map(source.getNumberOfLights(),
                Integer.class) : numberOfLights, !indexAddressMap.isEmpty() ? indexAddressMap : adresMap);
    }
}
